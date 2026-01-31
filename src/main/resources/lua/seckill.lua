local stockKey = KEYS[1]
local qualifiedSetKey = KEYS[2]
local purchasedSetKey = KEYS[3]
local userId = ARGV[1]
-- 商品不存在(再次检查)
if(redis.call('EXISTS', stockKey) == 0) then
    return 0
end
-- 检查是否第一次购买
if(redis.call('SISMEMBER', purchasedSetKey, userId) == 1) then
    --已经购买过了
    return 1
end

-- 库存充足
-- 把userId加入zset
local ret = redis.call('SADD', qualifiedSetKey, userId)
if(ret == 0) then
    -- 说明插入失败,证明已经再队列里面了,还没支付
    return 2
else
    -- 可以购买
    -- 库存-1
    redis.call('DECR', stockKey)
    return 3
end