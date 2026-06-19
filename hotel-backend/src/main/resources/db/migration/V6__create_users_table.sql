-- Create user_accounts table
CREATE TABLE user_accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    is_partner BOOLEAN DEFAULT FALSE,
    is_admin BOOLEAN DEFAULT FALSE,
    hotel_name VARCHAR(255),
    hotel_city VARCHAR(255),
    hotel_phone VARCHAR(255),
    hotel_status VARCHAR(255), -- 'pending', 'approved', 'rejected'
    verified BOOLEAN DEFAULT FALSE,
    verification_code VARCHAR(255),
    provider VARCHAR(50) DEFAULT 'local', -- 'local' or 'google'
    avatar_url TEXT,
    joined_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create refresh_tokens table
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    token VARCHAR(500) NOT NULL UNIQUE,
    user_email VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP NOT NULL
);

-- Seed static admin account (cannot be registered/changed)
INSERT INTO user_accounts (id, email, password, name, is_partner, is_admin, verified, provider, avatar_url)
VALUES (
    'd1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d',
    'admin@yme.lk',
    'password',
    'Chathura Silva (Admin)',
    FALSE,
    TRUE,
    TRUE,
    'local',
    'https://images.unsplash.com/photo-1560250097-0b93528c311a?w=100&h=100&fit=crop'
) ON CONFLICT (email) DO NOTHING;

-- Seed default partner (pre-verified, approved)
INSERT INTO user_accounts (id, email, password, name, is_partner, hotel_name, hotel_city, hotel_phone, hotel_status, verified, provider, avatar_url)
VALUES (
    'e1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d',
    'partner@yme.lk',
    'password',
    'Suresh Perera',
    TRUE,
    'Grand Paradise Resort',
    'Colombo',
    '+94 77 123 4567',
    'approved',
    TRUE,
    'local',
    'https://images.unsplash.com/photo-1542314831-c6a4d14abace?w=100&h=100&fit=crop'
) ON CONFLICT (email) DO NOTHING;

-- Seed default traveler (pre-verified)
INSERT INTO user_accounts (id, email, password, name, is_partner, verified, provider, avatar_url)
VALUES (
    'f2b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d',
    'user@yme.lk',
    'password',
    'Nimmi Alwis',
    FALSE,
    TRUE,
    'local',
    'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop'
) ON CONFLICT (email) DO NOTHING;
