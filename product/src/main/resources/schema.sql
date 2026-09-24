-- 字段沿用课堂SkuPO；身份由员工服务校验后写入，价格沿用课堂整数类型。
CREATE TABLE IF NOT EXISTS tb_sku (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    shop_id INTEGER NOT NULL CHECK (shop_id > 0),
    cate_name TEXT NOT NULL CHECK (length(trim(cate_name)) > 0),
    sku_name TEXT NOT NULL CHECK (length(trim(sku_name)) > 0),
    sell_point TEXT NOT NULL CHECK (length(trim(sell_point)) > 0),
    stock INTEGER NOT NULL CHECK (stock >= 0),
    price INTEGER NOT NULL CHECK (price > 0),
    status INTEGER NOT NULL DEFAULT 0 CHECK (status IN (0, 1)),
    type INTEGER NOT NULL CHECK (type IN (1, 2)),
    created_user_id INTEGER NOT NULL CHECK (created_user_id > 0),
    update_user_id INTEGER NOT NULL CHECK (update_user_id > 0),
    created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    updated_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    UNIQUE (id, shop_id)
);
CREATE INDEX IF NOT EXISTS idx_sku_shop ON tb_sku(shop_id, id);
-- 复合外键保证日志归属与商品店铺一致，日志和上架在同一事务内完成。
CREATE TABLE IF NOT EXISTS tb_sku_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sku_id INTEGER NOT NULL,
    shop_id INTEGER NOT NULL,
    stock INTEGER NOT NULL CHECK (stock >= 0),
    price INTEGER NOT NULL CHECK (price > 0),
    status INTEGER NOT NULL CHECK (status IN (0, 1)),
    created_user_id INTEGER NOT NULL CHECK (created_user_id > 0),
    created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    updated_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
    FOREIGN KEY (sku_id, shop_id) REFERENCES tb_sku(id, shop_id)
);
