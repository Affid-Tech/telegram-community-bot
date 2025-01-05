INSERT INTO global_properties (key, value)
VALUES ('globalFooter', '')
ON CONFLICT (key) DO NOTHING;