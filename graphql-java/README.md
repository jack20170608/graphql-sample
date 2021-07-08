##  使用graphql查询的简单示例

```json
{ "customerIds": [1, 2,3,4]
, "productIds": [1,2,3,4,5,6,7,8,9,10]
, "statusSet": ["PAY","DELIVERING","FINISHED"]
, "pageNum": 55
, "pageSize": 100
, "cacheQueryResult": true
, "queryKey": "2021062722420904900000"
, "orderFieldList": [ "id","sequenceNo"]
}

```

```json
{ "customerIds": [1]
, "productIds": [1]
, "statusSet": ["PAY","DELIVERING","FINISHED"]
, "pageNum": 374
, "pageSize": 100
, "cacheQueryResult": false
, "orderFieldList": [ "id","sequenceNo", "customerId", "productId"] 
}
```


