INSERT INTO test_cases (project_id, data_type_id, name, value, is_predefined) VALUES
-- Basic Valid String Test Cases
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Empty', '', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Single Whitespace', ' ', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Multiple Whitespace', '   ', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Alphanumeric', 'Hello World 123', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Special Chars', '!"#$%&''()*+,-./:;<=>?@[\\]^_`{|}~', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Unicode/Emoji', '你好世界 👋🌍', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Multi-line', 'Line 1\nLine 2\nLine 3', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Leading/Trailing Whitespace', '  padded string  ', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Null Value (Explicit)', NULL, TRUE),

-- Edge Cases / Unexpected Inputs (for downstream processing/display)
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Only Control Chars', E'\t\n\r\b', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: SQL Injection Attempt', ''' ; DROP TABLE users; --', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: XSS Attempt', '<script>alert("XSS")</script>', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: JSON-like', '{"status": "success", "data": [1,2]}', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Boolean-like True', 'true', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Boolean-like False', 'false', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Numeric-like Integer', '1234567890', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Numeric-like Decimal', '3.14159', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: UUID-like', '123e4567-e89b-12d3-a456-426614174000', TRUE),

-- Email-related Test Cases
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'Email: Valid Standard', 'test@example.com', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'Email: Valid Complex', 'first.last+tag@sub.domain.co.uk', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'Email: Invalid No At Sign', 'invalid-email.com', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'Email: Invalid No Domain', 'user@', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'Email: Invalid Multiple At Signs', 'user@domain@example.com', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'Email: Invalid Leading Dot', '.user@example.com', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'Email: Invalid Trailing Dot', 'user@example.com.', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'Email: Invalid Space', 'user name@example.com', TRUE),

-- Weak Password Test Cases
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'Password: Weak Too Short', 'abc', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'Password: Weak Common', 'password123', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'Password: Weak Sequential', '123456', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'Password: Weak All Lowercase', 'weakpassword', TRUE),
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'Password: Weak Simple Pattern', 'qwerty', TRUE),

-- Extreme Length (~1000 chars)
(NULL, 'ad6bdf76-cc93-40ae-ab1e-9e24a7cd56a2', 'String: Very Long (~1000 chars)',
 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',
 TRUE);




