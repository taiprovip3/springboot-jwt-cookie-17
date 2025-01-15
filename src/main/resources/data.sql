-- create roles
insert into roles (id, name) value (1, 'ADMIN');
insert into roles (id, name) value (2, 'MANAGER');
insert into roles (id, name) value (3, 'USER');

-- create permissions
insert into permissions (id, operation, resource) values (1, 'READ', 'USER');
insert into permissions (id, operation, resource) values (2, 'CREATE', 'USER');
insert into permissions (id, operation, resource) values (3, 'UPDATE', 'USER');
insert into permissions (id, operation, resource) values (4, 'DELETE', 'USER');

-- link roles with permissions
-- role admin permissions
insert into role_permission (role_id, permission_id) VALUES (1, 1);
insert into role_permission (role_id, permission_id) VALUES (1, 2);
insert into role_permission (role_id, permission_id) VALUES (1, 3);
insert into role_permission (role_id, permission_id) VALUES (1, 4);
-- role manager permissions
insert into role_permission (role_id, permission_id) VALUES (2, 1);
insert into role_permission (role_id, permission_id) VALUES (2, 2);
-- role user permissions
insert into role_permission (role_id, permission_id) VALUES (3, 1);

-- create catogory
insert into category (id, alias, cover_image, name) VALUES (1, 'MAINBOARD', 'https://nguyencongpc.vn/media/product/23544-mainboard-msi-meg-z790-ace-ddr5-6.jpeg', 'Mainboard');
insert into category (id, alias, cover_image, name) VALUES (2, 'CPU', 'https://product.hstatic.net/200000420363/product/cpu-amd-ryzen-5-1400-tray-600x600_8780ffbee6ba49c994f16988e2b88e96_master.jpg', 'CPU');
insert into category (id, alias, cover_image, name) VALUES (3, 'VGA', 'https://product.hstatic.net/200000478869/product/geforce_rtx__4080_16gb_eagle_oc-03_018bb9274286472d8954ed738343b51f_1024x1024.png', 'VGA');
insert into category (id, alias, cover_image, name) VALUES (4, 'PSU', 'https://www.buildcomputers.net/images/best-budget-power-supply.jpg', 'PSU');
insert into category (id, alias, cover_image, name) VALUES (5, 'RAM', 'https://static0.xdaimages.com/wordpress/wp-content/uploads/2023/03/adata-xpg-lancer-ddr5-ram-kit-product.png', 'RAM');
insert into category (id, alias, cover_image, name) VALUES (6, 'SSD', 'https://nguyencongpc.vn/media/product/17066-samsung-980-pro-1tb-1.JPG', 'SSD');
insert into category (id, alias, cover_image, name) VALUES (7, 'MONITOR', 'https://tblpro.com/Data/Sites/1/Product/411/1.png', 'Màn hình');
insert into category (id, alias, cover_image, name) VALUES (8, 'HEATSINK', 'https://tanthanhdanh.vn/wp-content/uploads/2024/04/masterliquid-240-atmos-gallery-01-zoom.png', 'Tản nhiệt');
insert into category (id, alias, cover_image, name) VALUES (9, 'MOUSE', 'https://onikumagaming.com/cdn/shop/products/ONIKUMA-CW905-6400-DPI-Wired-Gaming-Mouse-USB-Game-Mice-7-Buttons-Design-Breathing-LED-Colors-for-Laptop-PC-Gamer_1024x1024.png?v=1709624824', 'Chuột');
insert into category (id, alias, cover_image, name) VALUES (10, 'KEYBOARD', 'https://static0.xdaimages.com/wordpress/wp-content/uploads/2023/09/steelseries-apex-pro-tkl.png', 'Bàn phím');
insert into category (id, alias, cover_image, name) VALUES (11, 'ACCESSORY', 'https://static0.xdaimages.com/wordpress/wp-content/uploads/2023/09/steelseries-apex-pro-tkl.png', 'Phụ kiện');

-- create sub_category
insert into sub_category (id, alias, cover_image, name, category_id) VALUES (1, 'HEADPHONE', 'https://monsterstore.com/cdn/shop/products/gaming_headphones_clear.png?v=1674678826&width=1024', 'Tai nge', 11);
insert into sub_category (id, alias, cover_image, name, category_id) VALUES (2, 'SPEAKER', 'https://www.armaggeddon.com.my/image/sonicgear/image/cache/data/all_product_images/product-2799/Lumo-2.-420x420.png', 'Loa', 11);
insert into sub_category (id, alias, cover_image, name, category_id) VALUES (3, 'CABLE', 'https://cdn11.bigcommerce.com/s-t1wh9e/images/stencil/1280x1280/products/1072/10167/U4C-YLBK-01__14174.1718214473.png?c=2', 'Cáp', 11);
insert into sub_category (id, alias, cover_image, name, category_id) VALUES (4, 'FAN', 'https://product.hstatic.net/1000037809/product/thegioigear_xigmatek_x22f_1_ef59a9ae70224d2dab9c1f8c79547363_master.png', 'Fan case', 11);
insert into sub_category (id, alias, cover_image, name, category_id) VALUES (5, 'OTHERS', 'https://cdn11.bigcommerce.com/s-t1wh9e/images/stencil/1280x1280/products/1072/10167/U4C-YLBK-01__14174.1718214473.png?c=2', 'Khác', 11);

-- Category:
-- MAINBOARD, CPU, VGA, PSU, RAM, SSD, MONITOR, HEATSINK, ACCESSORY, TOWER_CASE, CASE_PC, FULL_PC
-- Sub Category:
-- ACCESSORY: MOUSE, KEYBOARD, EARPHONE, SPEAKER, CABLE, FAN, ADAPTER, OTHERS