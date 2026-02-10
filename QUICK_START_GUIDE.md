# Quick Start Guide: Extended Favorite Feature

## Prerequisites
Run the database migrations first:
```bash
mysql -u username -p database_name < migrations/001_add_favorite_polymorphic_fields.sql
mysql -u username -p database_name < migrations/002_create_brand_merchant_tables.sql
```

## API Usage

### 1. Add a Favorite

**Product Favorite:**
```bash
POST /v1/favorites
Content-Type: application/json

{
  "targetType": "PRODUCT",
  "targetId": 123,
  "type": "FAVORITE"
}
```

**Brand Favorite:**
```bash
POST /v1/favorites
Content-Type: application/json

{
  "targetType": "BRAND",
  "targetId": 456,
  "type": "FAVORITE"
}
```

**Merchant Favorite:**
```bash
POST /v1/favorites
Content-Type: application/json

{
  "targetType": "MERCHANT",
  "targetId": 789,
  "type": "FAVORITE"
}
```

**Backward Compatible (no targetType, defaults to PRODUCT):**
```bash
POST /v1/favorites
Content-Type: application/json

{
  "targetId": 123,
  "type": "FAVORITE"
}
```

### 2. Remove a Favorite

Same as adding, but with `"type": "UNFAVORITE"`:

```bash
POST /v1/favorites
Content-Type: application/json

{
  "targetType": "BRAND",
  "targetId": 456,
  "type": "UNFAVORITE"
}
```

### 3. Get Favorites

**Get Product Favorites (default):**
```bash
GET /v1/favorites?offset=0&limit=20
```

**Get Product Favorites (explicit):**
```bash
GET /v1/favorites?targetType=PRODUCT&offset=0&limit=20
```

**Get Brand Favorites:**
```bash
GET /v1/favorites?targetType=BRAND&offset=0&limit=20
```

**Get Merchant Favorites:**
```bash
GET /v1/favorites?targetType=MERCHANT&offset=0&limit=20
```

## Response Format

All responses follow the same structure, but with different fields populated based on the target type:

```json
{
  "code": "0000",
  "message": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "targetType": "PRODUCT",
        "targetId": 123,
        "name": "iPhone 15 Pro",
        "imageUrl": "https://example.com/image.jpg",
        "description": "Latest iPhone model",
        "costPrice": 900.00,
        "salesPrice": 1200.00,
        "discountedPrice": 1100.00,
        "favoritedAt": "2026-02-10T10:30:00"
      }
    ],
    "hasNext": true
  }
}
```

**Note:** For Brand and Merchant favorites, the price fields (`costPrice`, `salesPrice`, `discountedPrice`) will be `null`.

## Testing Checklist

- [ ] Add product favorite
- [ ] Add brand favorite
- [ ] Add merchant favorite
- [ ] Get favorites by each type (PRODUCT, BRAND, MERCHANT)
- [ ] Remove favorites
- [ ] Test pagination (offset/limit)
- [ ] Verify soft delete (check database status column)
- [ ] Test idempotency (favorite same item twice)
- [ ] Verify backward compatibility (API calls without targetType)
- [ ] Check that only active favorites are returned
- [ ] Verify 30-day cutoff filter

## Backward Compatibility

✅ **All existing API clients continue to work without changes**

If you omit the `targetType` parameter:
- GET requests default to `targetType=PRODUCT`
- POST requests default to `targetType=PRODUCT`

## Database Schema

### Favorite Table (Updated)
```sql
CREATE TABLE favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    target_type VARCHAR(50) DEFAULT 'PRODUCT',
    target_id BIGINT,
    product_id BIGINT,  -- Deprecated, kept for compatibility
    favorited_at TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_favorite_target (target_type, target_id, status),
    INDEX idx_favorite_user_target (user_id, target_type, status, updated_at)
);
```

### Brand Table (New)
```sql
CREATE TABLE brand (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo_url VARCHAR(500),
    description TEXT,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_brand_status (status)
);
```

### Merchant Table (New)
```sql
CREATE TABLE merchant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo_url VARCHAR(500),
    description TEXT,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_merchant_status (status)
);
```

## Common Issues and Solutions

### Issue: "Product/Brand/Merchant not found"
**Cause:** The target entity doesn't exist or is marked as DELETED
**Solution:** Ensure the target entity exists and has status='ACTIVE'

### Issue: Favorites not appearing in GET requests
**Cause:** Favorites older than 30 days or status is not 'ACTIVE'
**Solution:** Check the `updated_at` timestamp and `status` column

### Issue: Duplicate favorites
**Note:** This is expected behavior - adding the same favorite twice updates the `favoritedAt` timestamp

## Performance Considerations

- All queries use indexed columns (target_type, target_id, status, updated_at)
- Batch fetching is used to avoid N+1 queries
- 30-day cutoff reduces query result size
- Soft delete means no physical deletion overhead

## Monitoring

Key metrics to monitor:
- Favorite creation rate by type
- Favorite removal rate by type
- Query performance on favorite table
- Distribution of favorites across types (PRODUCT vs BRAND vs MERCHANT)

## Future Enhancements

Potential improvements for future iterations:
- Add search/filter functionality for favorites
- Add sorting options (by name, date, etc.)
- Add favorite count to Brand/Merchant entities
- Create aggregate views for analytics
- Remove deprecated `product_id` column after migration verification
