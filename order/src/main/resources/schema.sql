-- 沿用课堂订单字段；7A每单一件商品，商品金额使用Product返回的整数报价。
CREATE TABLE IF NOT EXISTS tb_order (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    shop_id INTEGER NOT NULL CHECK (shop_id > 0),
    status TEXT NOT NULL CHECK (status IN ('WAIT_PAY','PAID','SENT','COMPLETED')),
    sku_total INTEGER NOT NULL CHECK (sku_total = 1),
    sku_total_price INTEGER NOT NULL CHECK (sku_total_price > 0),
    created_user_id INTEGER NOT NULL CHECK (created_user_id > 0),
    update_user_id INTEGER NOT NULL CHECK (update_user_id > 0),
    created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    updated_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    UNIQUE (id, shop_id)
);
CREATE INDEX IF NOT EXISTS idx_order_shop ON tb_order(shop_id, id);
-- 商品快照与订单在同一库、同一事务；不建立跨服务数据库外键。
CREATE TABLE IF NOT EXISTS tb_order_sku (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL UNIQUE,
    shop_id INTEGER NOT NULL,
    sku_id INTEGER NOT NULL CHECK (sku_id > 0),
    cate_name TEXT NOT NULL,
    sku_name TEXT NOT NULL,
    sell_point TEXT NOT NULL,
    type INTEGER NOT NULL CHECK (type IN (1, 2)),
    price INTEGER NOT NULL CHECK (price > 0),
    created_user_id INTEGER NOT NULL CHECK (created_user_id > 0),
    created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    updated_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    FOREIGN KEY (order_id, shop_id) REFERENCES tb_order(id, shop_id)
);

-- 失败尝试保留历史；同一订单至多一条待确认或成功支付。
CREATE TABLE IF NOT EXISTS tb_payment (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL,
    shop_id INTEGER NOT NULL,
    pay_type TEXT NOT NULL CHECK (pay_type IN ('ALIPAY', 'WECHAT')),
    amount INTEGER NOT NULL CHECK (amount > 0),
    status TEXT NOT NULL CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED')),
    channel_trade_no TEXT NOT NULL UNIQUE,
    created_user_id INTEGER NOT NULL CHECK (created_user_id > 0),
    update_user_id INTEGER NOT NULL CHECK (update_user_id > 0),
    confirmed_at TEXT,
    created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    updated_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    FOREIGN KEY (order_id, shop_id) REFERENCES tb_order(id, shop_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_payment_active ON tb_payment(order_id) WHERE status IN ('PENDING','SUCCESS');
CREATE TABLE IF NOT EXISTS tb_order_status_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL,
    shop_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    from_status TEXT NOT NULL,
    to_status TEXT NOT NULL,
    event TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ','now')),
    UNIQUE(order_id,to_status),
    FOREIGN KEY(order_id,shop_id) REFERENCES tb_order(id,shop_id)
);
