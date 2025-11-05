-- ========================================
-- PHASE 1.9: MVP seed data (H2 & SQLite Compatible)
-- ========================================

-- Users (upsert to avoid duplicates)
MERGE INTO users (id, email, name, last_name, password_hash, created_at) KEY(id) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'admin@example.com', 'Admin', 'User', '$2a$10$N9qo8uLOickgx2ZMRZoMy.8g3LjQxZG5p7pT8LUmtA5dF0WqK0Fwy', CURRENT_TIMESTAMP),
('660e8400-e29b-41d4-a716-446655440001', 'john.doe@example.com', 'John', 'Doe', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHe1VCYnlhBqT1r8p6cDK8Zm2L8d0YFe1u', CURRENT_TIMESTAMP);

-- Categories (upsert to avoid duplicates)
MERGE INTO categories (id, name, color, user_id) KEY(id) VALUES
('11111111-1111-1111-1111-111111111111', 'Food', '#FF5733', '550e8400-e29b-41d4-a716-446655440000'),
('22222222-2222-2222-2222-222222222222', 'Health', '#33C3FF', '550e8400-e29b-41d4-a716-446655440000'),
('33333333-3333-3333-3333-333333333333', 'Transport', '#FFD700', '550e8400-e29b-41d4-a716-446655440000'),
('44444444-4444-4444-4444-444444444444', 'Food', '#FF5733', '660e8400-e29b-41d4-a716-446655440001');

-- TransactionsV2 (upsert to avoid duplicates)
MERGE INTO transactionsv2 (id, amount, currency, date, note, user_id, category_id, created_at, updated_at) KEY(id) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 45.50, 'USD', DATEADD('DAY', -5, CURRENT_DATE), 'Grocery shopping', '550e8400-e29b-41d4-a716-446655440000', '11111111-1111-1111-1111-111111111111', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 120.00, 'EUR', DATEADD('DAY', -3, CURRENT_DATE), 'Monthly gym', '550e8400-e29b-41d4-a716-446655440000', '22222222-2222-2222-2222-222222222222', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 89.99, 'USD', DATEADD('DAY', -1, CURRENT_DATE), 'Team dinner', '550e8400-e29b-41d4-a716-446655440000', '11111111-1111-1111-1111-111111111111', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 25.00, 'USD', CURRENT_DATE, 'Lunch', '660e8400-e29b-41d4-a716-446655440001', '44444444-4444-4444-4444-444444444444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
-- ========================================