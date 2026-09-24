-- 核心字段依据老师 ShopPO/ShopMapper；唯一约束和初始化状态依据本项目验收要求。
-- 课堂店铺名上限为6；密码列存BCrypt结果，不存注册明文。
CREATE TABLE IF NOT EXISTS tb_shop (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    shop_name TEXT NOT NULL UNIQUE CHECK (length(shop_name) BETWEEN 1 AND 6),
    admin_account TEXT NOT NULL CHECK (length(trim(admin_account)) > 0),
    admin_password TEXT NOT NULL,
    logo_url TEXT,
    status INTEGER NOT NULL DEFAULT 1,
    init_status TEXT NOT NULL DEFAULT 'PENDING'
        CHECK (init_status IN ('PENDING', 'COMPLETED', 'FAILED')),
    created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    updated_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now'))
);

-- 欢迎消息属于店铺库，员工库由 Employee 服务独立维护。
CREATE TABLE IF NOT EXISTS tb_messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    shop_id INTEGER NOT NULL REFERENCES tb_shop(id),
    sender_id INTEGER,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    msg_type INTEGER NOT NULL,
    is_read INTEGER NOT NULL DEFAULT 0 CHECK (is_read IN (0, 1)),
    read_time TEXT,
    created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    updated_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now'))
);
-- 沿用课堂 msg_type=1 表示欢迎消息：每个店铺至多一条，其他消息类型不受此约束。
CREATE UNIQUE INDEX IF NOT EXISTS uq_shop_welcome ON tb_messages(shop_id) WHERE msg_type = 1;

-- 新库初始化任务；执行编号和截止时间用于重试竞争、进程中断后的恢复。
CREATE TABLE IF NOT EXISTS tb_shop_initialization_task (
    shop_id INTEGER PRIMARY KEY REFERENCES tb_shop(id) ON DELETE CASCADE,
    attempt INTEGER NOT NULL DEFAULT 0 CHECK (attempt >= 0),
    deadline_epoch_ms INTEGER NOT NULL CHECK (deadline_epoch_ms >= 0),
    employee_initialized INTEGER NOT NULL DEFAULT 0 CHECK (employee_initialized IN (0,1)),
    failure_code TEXT
);
