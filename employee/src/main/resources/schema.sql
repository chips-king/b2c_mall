-- 字段参考老师 EmployeePO；跨服务 shop_id 是业务关联，不建立跨库外键。
CREATE TABLE IF NOT EXISTS tb_employee (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    shop_id INTEGER NOT NULL CHECK (shop_id > 0),
    username TEXT NOT NULL CHECK (length(trim(username)) > 0),
    password TEXT NOT NULL,
    avatar_url TEXT,
    last_login_time TEXT,
    login_count INTEGER NOT NULL DEFAULT 0,
    status INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    updated_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    UNIQUE (shop_id, username)
);

-- 未知账号的失败登录也需审计，因此employee_id允许为空且不依赖员工外键。
CREATE TABLE IF NOT EXISTS tb_login_audit (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    shop_id INTEGER NOT NULL CHECK (shop_id > 0),
    employee_id INTEGER,
    username TEXT NOT NULL,
    remote_address TEXT NOT NULL,
    outcome TEXT NOT NULL CHECK (outcome IN ('SUCCESS', 'FAILURE')),
    reason_code TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now'))
);
CREATE INDEX IF NOT EXISTS idx_login_audit_employee ON tb_login_audit(shop_id, employee_id, created_at);
