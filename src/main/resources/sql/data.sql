INSERT INTO test_cases (project_id, data_type_id, name, value, is_predefined) VALUES

-- [ STRING ]
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




-- [ INTEGER ]
-- Basic Valid Integer Test Cases
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Zero', '0', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Small Positive', '1', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Small Negative', '-1', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Large Positive', '999999', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Large Negative', '-999999', TRUE),

-- Boundary Value Test Cases (32-bit & 64-bit)
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: 32-bit Max', '2147483647', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: 32-bit Min', '-2147483648', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: 64-bit Max', '9223372036854775807', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: 64-bit Min', '-9223372036854775808', TRUE),

-- Invalid Integer Inputs (Common Mistakes)
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Decimal Number', '3.14', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Negative Decimal', '-0.001', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Alphanumeric', '123abc', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Text String', 'notanumber', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Boolean-like True', 'true', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Boolean-like False', 'false', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Empty String', '', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Single Whitespace', ' ', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Null Value (Explicit)', NULL, TRUE),

-- Injection and Special Characters
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: SQL Injection Attempt', '''; DROP TABLE users; --', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: XSS Attempt', '<script>1+1</script>', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Special Characters', '!@#$%^&*()', TRUE),

-- Overflow & Extreme Input
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Above 64-bit Max', '9999999999999999999999999999', TRUE),
(NULL, '4d1d80b2-1941-40e7-9960-db67a2231674', 'Integer: Below 64-bit Min', '-9999999999999999999999999999', TRUE);




-- [ DATE ]
-- Valid Date Formats (ISO & common)
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: ISO Format (YYYY-MM-DD)', '2025-06-19', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Leap Year Feb 29', '2024-02-29', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Start of Unix Epoch', '1970-01-01', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Far Past (Year 0001)', '0001-01-01', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Far Future', '9999-12-31', TRUE),

-- Ambiguous or Region-Specific Formats
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: US Format (MM/DD/YYYY)', '06/19/2025', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: European Format (DD.MM.YYYY)', '19.06.2025', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: With Slashes (YYYY/MM/DD)', '2025/06/19', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: With Text Month', 'June 19, 2025', TRUE),

-- Invalid or Corrupted Dates
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Invalid Month 13', '2025-13-01', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Invalid Day 32', '2025-01-32', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Non-Leap Feb 29', '2023-02-29', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Invalid Format Letters', 'notadate', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Partial Date', '2025-06', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Empty String', '', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Null Value (Explicit)', NULL, TRUE),

-- Malicious/Unexpected Inputs
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: SQL Injection Attempt', '''; DROP TABLE test_cases; --', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Script Injection', '<script>delete()</script>', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Boolean-like String', 'true', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Random Numbers', '1234567890', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Special Characters', '!@#$%^&*()', TRUE),

-- Timestamps (if backend supports parsing them)
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: ISO Timestamp (Date + Time)', '2025-06-19T15:45:00Z', TRUE),
(NULL, '25ebb940-32e1-4916-9a0d-55b9da4e8722', 'Date: Only Time', '15:45:00', TRUE);




-- [ FILE ]
-- Valid File Representations (by name or description)
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Small Text File', 'file.txt (1KB)', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Valid PDF', 'document.pdf (45KB)', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Image PNG', 'image.png (512KB)', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Image JPEG', 'photo.jpg (1MB)', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Excel Spreadsheet', 'data.xlsx (22KB)', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Valid ZIP Archive', 'archive.zip (200KB)', TRUE),

-- Edge Cases
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Empty File', 'empty.txt (0B)', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: File With No Extension', 'file (13KB)', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: File With Double Extension', 'file.jpg.exe', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Uppercase Extension', 'photo.PNG', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Extremely Large File', 'video.mp4 (4GB)', TRUE),

-- Invalid / Malicious Input
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Unsupported Extension', 'script.sh', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: EXE File (Dangerous)', 'virus.exe', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: HTML Disguised as Image', 'image.jpg (actually <html>)', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Embedded JS in PDF', 'exploit.pdf', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: SQL Injection Filename', '''; DROP TABLE users;--.txt', TRUE),

-- Format Confusion / Bypass Attempts
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Filename With Emoji', '🧪test.pdf', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Filename With Unicode', 'résumé.docx', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Hidden Executable', 'photo.jpg    .exe', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Spaces In Filename', 'my file.pdf', TRUE),
(NULL, '5ff8e890-6f8f-4fc6-a69b-b1429c272bf6', 'File: Null Value (Explicit)', NULL, TRUE);




-- [ BOOLEAN ]
-- Valid Boolean Values
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: True (literal)', 'true', TRUE),
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: False (literal)', 'false', TRUE),
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: True (uppercase)', 'TRUE', TRUE),
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: False (uppercase)', 'FALSE', TRUE),
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: Numeric 1 (true)', '1', TRUE),
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: Numeric 0 (false)', '0', TRUE),

-- Invalid or Unexpected Inputs
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: Numeric other than 0/1', '2', TRUE),
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: Yes', 'yes', TRUE),
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: No', 'no', TRUE),
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: On', 'on', TRUE),
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: Off', 'off', TRUE),
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: Empty String', '', TRUE),
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: Whitespace', ' ', TRUE),
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: Null Value (Explicit)', NULL, TRUE),

-- Injection and Special Characters
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: SQL Injection Attempt', '''; DROP TABLE users; --', TRUE),
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: Script Injection', '<script>alert("XSS")</script>', TRUE),
(NULL, '892462cc-492b-44a9-83b8-1c38a3c94623', 'Boolean: Special Characters', '!@#$%^&*()', TRUE);




-- [ UUID ]
-- Valid UUIDs (version 1 and 4 examples)
(NULL, '3d831b97-fdb7-4a52-a05e-6e7da07fe2b2', 'UUID: Valid v4 Standard', '550e8400-e29b-41d4-a716-446655440000', TRUE),
(NULL, '3d831b97-fdb7-4a52-a05e-6e7da07fe2b2', 'UUID: Valid v1 Example', '6ba7b810-9dad-11d1-80b4-00c04fd430c8', TRUE),

-- Invalid UUIDs
(NULL, '3d831b97-fdb7-4a52-a05e-6e7da07fe2b2', 'UUID: Missing Sections', '550e8400-e29b-41d4-a716', TRUE),
(NULL, '3d831b97-fdb7-4a52-a05e-6e7da07fe2b2', 'UUID: Invalid Characters', 'zzzzzzzz-zzzz-zzzz-zzzz-zzzzzzzzzzzz', TRUE),
(NULL, '3d831b97-fdb7-4a52-a05e-6e7da07fe2b2', 'UUID: Too Short', '1234-5678', TRUE),
(NULL, '3d831b97-fdb7-4a52-a05e-6e7da07fe2b2', 'UUID: Too Long', '550e8400-e29b-41d4-a716-4466554400001234', TRUE),
(NULL, '3d831b97-fdb7-4a52-a05e-6e7da07fe2b2', 'UUID: Empty String', '', TRUE),
(NULL, '3d831b97-fdb7-4a52-a05e-6e7da07fe2b2', 'UUID: Null Value (Explicit)', NULL, TRUE),

-- Injection and Malicious Inputs
(NULL, '3d831b97-fdb7-4a52-a05e-6e7da07fe2b2', 'UUID: SQL Injection Attempt', '''; DROP TABLE users; --', TRUE),
(NULL, '3d831b97-fdb7-4a52-a05e-6e7da07fe2b2', 'UUID: Script Injection', '<script>alert("XSS")</script>', TRUE);




-- [ ENUM ]
-- Valid Enum Values (example enum options)
(NULL, '0c2ad487-b32f-421c-91cc-5b102a337f5e', 'Enum: Valid Option 1', 'OPTION_A', TRUE),
(NULL, '0c2ad487-b32f-421c-91cc-5b102a337f5e', 'Enum: Valid Option 2', 'OPTION_B', TRUE),
(NULL, '0c2ad487-b32f-421c-91cc-5b102a337f5e', 'Enum: Valid Option 3', 'OPTION_C', TRUE),

-- Case Sensitivity Checks
(NULL, '0c2ad487-b32f-421c-91cc-5b102a337f5e', 'Enum: Lowercase Option', 'option_a', TRUE),
(NULL, '0c2ad487-b32f-421c-91cc-5b102a337f5e', 'Enum: Mixed Case Option', 'Option_B', TRUE),

-- Invalid Enum Values
(NULL, '0c2ad487-b32f-421c-91cc-5b102a337f5e', 'Enum: Invalid Option', 'OPTION_XYZ', TRUE),
(NULL, '0c2ad487-b32f-421c-91cc-5b102a337f5e', 'Enum: Numeric String', '123', TRUE),
(NULL, '0c2ad487-b32f-421c-91cc-5b102a337f5e', 'Enum: Empty String', '', TRUE),
(NULL, '0c2ad487-b32f-421c-91cc-5b102a337f5e', 'Enum: Whitespace', ' ', TRUE),
(NULL, '0c2ad487-b32f-421c-91cc-5b102a337f5e', 'Enum: Null Value (Explicit)', NULL, TRUE),

-- Injection and Special Characters
(NULL, '0c2ad487-b32f-421c-91cc-5b102a337f5e', 'Enum: SQL Injection Attempt', '''; DROP TABLE enum_values; --', TRUE),
(NULL, '0c2ad487-b32f-421c-91cc-5b102a337f5e', 'Enum: Script Injection', '<script>alert("XSS")</script>', TRUE),
(NULL, '0c2ad487-b32f-421c-91cc-5b102a337f5e', 'Enum: Special Characters', '!@#$%^&*()', TRUE);




-- [ ARRAY ]
-- Empty Array
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Empty Array', '[]', TRUE),

-- Non-Empty Arrays
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Integer Array', '[1]', TRUE),
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: String Array', '["1"]', TRUE),
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Boolean Array', '[true,false]', TRUE),
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Mixed Data Types', '[1, "string", true]', TRUE),

-- Nested Arrays
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Nested Arrays', '[[1,2], [3,4]]', TRUE),

-- Duplicate Elements
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Duplicate Elements', '[1, 2, 2]', TRUE),

-- Null Elements in Arrays
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Integer with Null', '[1, null]', TRUE),
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: String with Null', '["1", null]', TRUE),
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Boolean with Null', '[true, null]', TRUE),

-- Edge Cases
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Array with Nulls', '[null, null]', TRUE),
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Large Array', '[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]', TRUE),

-- Invalid Arrays
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Missing Closing Bracket', '[1, 2, 3', TRUE),
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Missing Opening Bracket', '1, 2, 3]', TRUE),
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Improper Quotes', '["apple, banana]', TRUE),
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Non-JSON Format', '1,2,3,4', TRUE),
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Empty String', '', TRUE),
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Null Value (Explicit)', NULL, TRUE),

-- Injection and Malicious Inputs
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: SQL Injection Attempt', '''; DROP TABLE users; --', TRUE),
(NULL, 'a3517091-944a-4058-b3fb-e713f03eb86a', 'Array: Script Injection', '<script>alert("XSS")</script>', TRUE);