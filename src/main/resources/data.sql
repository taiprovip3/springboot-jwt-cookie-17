-- create roles
insert ignore into roles (id, name) value (1, 'ADMIN');
insert ignore into roles (id, name) value (2, 'MANAGER');
insert ignore into roles (id, name) value (3, 'USER');

-- create permissions
insert ignore into permissions (id, operation, resource) values (1, 'READ', 'USER');
insert ignore into permissions (id, operation, resource) values (2, 'CREATE', 'USER');
insert ignore into permissions (id, operation, resource) values (3, 'UPDATE', 'USER');
insert ignore into permissions (id, operation, resource) values (4, 'DELETE', 'USER');

-- link roles with permissions
-- role admin permissions
insert ignore into role_permission (role_id, permission_id) VALUES (1, 1);
insert ignore into role_permission (role_id, permission_id) VALUES (1, 2);
insert ignore into role_permission (role_id, permission_id) VALUES (1, 3);
insert ignore into role_permission (role_id, permission_id) VALUES (1, 4);
-- role manager permissions
insert ignore into role_permission (role_id, permission_id) VALUES (2, 1);
insert ignore into role_permission (role_id, permission_id) VALUES (2, 2);
-- role user permissions
insert ignore into role_permission (role_id, permission_id) VALUES (3, 1);