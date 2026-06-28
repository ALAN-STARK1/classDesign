-- 演示数据初始化脚本
-- 账号: admin/admin123, alice/user123, bob/user123, carol/user123

-- ========== 用户账号 ==========
INSERT INTO sys_user (id, username, email, password_hash, role, status, created_at) VALUES
(1, 'admin', 'admin@example.com', '$2b$10$.6SQaxPdU5qJLDyqS6RDruKPvUg1aFHAbWLSk65vnvC/mIrkEef4y', 'ADMIN', 'ENABLED', '2026-06-01 09:00:00'),
(2, 'alice', 'alice@example.com', '$2b$10$KWn27SkEt3ey.nUMko7x7ui/Qu3.pkTESwnX0oGgefdsPyzubMM3e', 'USER', 'ENABLED', '2026-06-01 10:00:00'),
(3, 'bob', 'bob@example.com', '$2b$10$KWn27SkEt3ey.nUMko7x7ui/Qu3.pkTESwnX0oGgefdsPyzubMM3e', 'USER', 'ENABLED', '2026-06-02 10:00:00'),
(4, 'carol', 'carol@example.com', '$2b$10$KWn27SkEt3ey.nUMko7x7ui/Qu3.pkTESwnX0oGgefdsPyzubMM3e', 'USER', 'ENABLED', '2026-06-03 10:00:00'),
(5, 'mika', 'mika@example.com', '$2b$10$KWn27SkEt3ey.nUMko7x7ui/Qu3.pkTESwnX0oGgefdsPyzubMM3e', 'USER', 'ENABLED', '2026-06-04 10:00:00');

INSERT INTO user_profile (id, user_id, nickname, avatar_url, bio, gender, birthday, created_at, updated_at) VALUES
(1, 1, '管理员', '/avatars/admin.png', '系统管理员', 'UNKNOWN', NULL, '2026-06-01 09:00:00', '2026-06-01 09:00:00'),
(2, 2, 'Alice', '/avatars/alice.png', '减脂进行中', 'FEMALE', '2000-01-01', '2026-06-01 10:00:00', '2026-06-01 10:00:00'),
(3, 3, 'Bob', '/avatars/bob.png', '增肌爱好者', 'MALE', '1998-05-15', '2026-06-02 10:00:00', '2026-06-02 10:00:00'),
(4, 4, 'Carol', '/avatars/carol.png', '控糖饮食', 'FEMALE', '1995-08-20', '2026-06-03 10:00:00', '2026-06-03 10:00:00'),
(5, 5, 'Mika', '/avatars/mika.png', '美食分享', 'FEMALE', '1999-03-12', '2026-06-04 10:00:00', '2026-06-04 10:00:00');

-- ========== 健康档案 ==========
INSERT INTO health_profile (id, user_id, gender, birthday, height_cm, weight_kg, target_weight_kg, activity_level, health_goal, bmi, daily_calorie_target) VALUES
(1, 2, 'FEMALE', '2000-01-01', 165.00, 62.00, 56.00, 'LIGHT', 'FAT_LOSS', 22.77, 1600),
(2, 3, 'MALE', '1998-05-15', 178.00, 75.00, 80.00, 'MODERATE', 'MUSCLE_GAIN', 23.67, 2800),
(3, 4, 'FEMALE', '1995-08-20', 160.00, 58.00, 55.00, 'SEDENTARY', 'SUGAR_CONTROL', 22.66, 1700);

INSERT INTO user_preference (id, user_id, liked_tags, disliked_ingredients, preferred_cuisines, spice_level, created_at, updated_at) VALUES
(1, 2, '["高蛋白","低脂","轻食"]', '["香菜"]', '["中式","日式"]', 'MILD', '2026-06-01 10:00:00', '2026-06-01 10:00:00'),
(2, 3, '["高蛋白","增肌"]', '[]', '["西式"]', 'MEDIUM', '2026-06-02 10:00:00', '2026-06-02 10:00:00'),
(3, 4, '["低糖","高纤维"]', '[]', '["中式"]', 'MILD', '2026-06-03 10:00:00', '2026-06-03 10:00:00');

INSERT INTO user_allergen (id, user_id, allergen_name) VALUES
(1, 2, '花生'),
(2, 2, '虾');

INSERT INTO user_diet_restriction (id, user_id, restriction_name) VALUES
(1, 2, '少盐'),
(2, 4, '少糖');

INSERT INTO weight_record (id, user_id, record_date, weight_kg, created_at) VALUES
(1, 2, '2026-06-24', 62.00, '2026-06-24 08:00:00'),
(2, 2, '2026-06-28', 59.80, '2026-06-28 08:00:00'),
(3, 2, '2026-07-05', 59.20, '2026-07-05 08:00:00'),
(4, 3, '2026-06-24', 75.00, '2026-06-24 08:00:00'),
(5, 4, '2026-06-24', 58.00, '2026-06-24 08:00:00');

INSERT INTO health_goal_cycle (id, user_id, goal_type, start_date, end_date, start_weight_kg, target_weight_kg, target_calorie, weekly_target_delta_kg, progress_percent, status, created_at) VALUES
(10, 2, 'FAT_LOSS', '2026-06-24', '2026-08-19', 62.00, 56.00, 1600, -0.75, 35.50, 'ACTIVE', '2026-06-24 09:00:00'),
(11, 4, 'SUGAR_CONTROL', '2026-06-01', '2026-06-28', 58.00, 55.00, 1700, -0.50, 60.00, 'ACTIVE', '2026-06-01 09:00:00'),
(12, 3, 'MUSCLE_GAIN', '2026-05-01', '2026-07-31', 72.00, 80.00, 2800, 0.50, 45.00, 'ACTIVE', '2026-05-01 09:00:00');

-- ========== 食材 ==========
INSERT INTO ingredient (id, name, category, unit, calorie, protein, fat, carbohydrate, sodium, status) VALUES
(1,  '鸡胸肉', '肉类', 'g', 133.00, 24.60, 1.90, 2.50, 34.00, 'ENABLED'),
(2,  '西兰花', '蔬菜', 'g', 34.00,  2.80, 0.40, 6.60, 33.00, 'ENABLED'),
(3,  '番茄',   '蔬菜', 'g', 18.00,  0.90, 0.20, 3.90, 5.00,  'ENABLED'),
(4,  '鸡蛋',   '蛋类', 'g', 143.00, 12.60, 9.50, 0.70, 142.00,'ENABLED'),
(5,  '米饭',   '主食', 'g', 116.00, 2.60,  0.30, 25.90, 1.00, 'ENABLED'),
(6,  '燕麦',   '主食', 'g', 389.00, 16.90, 6.90, 66.30, 2.00, 'ENABLED'),
(7,  '牛奶',   '乳制品', 'g', 54.00, 3.20, 3.20, 4.80, 44.00, 'ENABLED'),
(8,  '苹果',   '水果', 'g', 52.00,  0.30, 0.20, 13.80, 1.00, 'ENABLED'),
(9,  '花生',   '坚果', 'g', 567.00, 25.80, 49.20, 16.10, 18.00, 'ENABLED'),
(10, 'Brown Rice', '主食', 'g', 111.00, 2.60, 0.90, 23.00, 5.00, 'ENABLED');

-- ========== 菜谱 ==========
INSERT INTO recipe (id, user_id, name, description, category, difficulty, cook_minutes, servings, total_calorie, total_protein, total_fat, total_carbohydrate, status) VALUES
(1001, NULL, '鸡胸肉蔬菜碗', '高蛋白低脂晚餐', 'DINNER', 'EASY', 20, 1, 360.00, 36.00, 8.00, 32.00, 'ONLINE'),
(1002, NULL, '番茄炒蛋', '经典家常菜', 'LUNCH', 'EASY', 15, 2, 280.00, 18.00, 20.00, 12.00, 'ONLINE'),
(1003, NULL, '燕麦牛奶早餐', '简单营养早餐', 'BREAKFAST', 'EASY', 10, 1, 320.00, 14.00, 10.00, 45.00, 'ONLINE'),
(1004, NULL, '清炒西兰花', '低卡蔬菜', 'DINNER', 'EASY', 12, 1, 120.00, 6.00, 5.00, 10.00, 'ONLINE'),
(1005, NULL, '清炒西兰花鸡蛋', '高蛋白晚餐', 'DINNER', 'EASY', 15, 1, 340.00, 28.00, 18.00, 12.00, 'ONLINE'),
(1006, NULL, '苹果轻食', '低糖加餐', 'SNACK', 'EASY', 5, 1, 104.00, 0.60, 0.40, 27.60, 'ONLINE'),
(1007, NULL, 'Brown Rice Bowl', '控糖主食', 'LUNCH', 'NORMAL', 25, 1, 350.00, 12.00, 6.00, 55.00, 'ONLINE'),
(1008, NULL, '增肌蛋白餐', '高蛋白增肌', 'LUNCH', 'NORMAL', 30, 1, 520.00, 48.00, 15.00, 40.00, 'ONLINE');

INSERT INTO recipe_ingredient (id, recipe_id, ingredient_id, amount_g, remark) VALUES
(1, 1001, 1, 120.00, '切片'),
(2, 1001, 2, 150.00, '焯水'),
(3, 1001, 10, 80.00, '熟重'),
(4, 1002, 3, 200.00, '切块'),
(5, 1002, 4, 100.00, '打散'),
(6, 1003, 6, 50.00, '即食燕麦'),
(7, 1003, 7, 200.00, '脱脂'),
(8, 1004, 2, 200.00, NULL),
(9, 1005, 2, 150.00, NULL),
(10, 1005, 4, 100.00, NULL),
(11, 1006, 8, 200.00, NULL),
(12, 1007, 10, 150.00, NULL),
(13, 1007, 1, 100.00, NULL),
(14, 1008, 1, 200.00, NULL),
(15, 1008, 5, 150.00, NULL);

INSERT INTO recipe_step (id, recipe_id, step_no, content) VALUES
(1, 1001, 1, '鸡胸肉切片并腌制。'),
(2, 1001, 2, '西兰花焯水后捞出。'),
(3, 1001, 3, '平底锅少油煎熟鸡胸肉，与蔬菜、糙米饭装盘。'),
(4, 1002, 1, '番茄切块，鸡蛋打散。'),
(5, 1002, 2, '先炒蛋盛出，再炒番茄，最后合炒。'),
(6, 1003, 1, '燕麦加入温牛奶，微波加热2分钟。'),
(7, 1004, 1, '西兰花焯水后清炒。'),
(8, 1005, 1, '西兰花焯水，鸡蛋炒熟，一起翻炒。');

INSERT INTO recipe_favorite (id, user_id, recipe_id, created_at) VALUES
(1, 2, 1001, '2026-06-20 10:00:00'),
(2, 2, 1003, '2026-06-21 10:00:00'),
(3, 3, 1008, '2026-06-22 10:00:00');

INSERT INTO recipe_suitability_score (id, user_id, recipe_id, score, calorie_score, macro_score, preference_score, risk_score, reason, calculated_at) VALUES
(1, 2, 1001, 88, 90, 86, 85, 95, '热量接近晚餐目标，蛋白质充足，适合减脂。', '2026-06-24 10:00:00'),
(2, 2, 1003, 82, 85, 80, 80, 90, '早餐热量适中。', '2026-06-24 10:00:00'),
(3, 2, 1005, 90, 88, 90, 88, 95, '热量接近原计划，蛋白质更高。', '2026-06-24 10:00:00'),
(4, 3, 1008, 92, 90, 95, 88, 90, '高蛋白适合增肌目标。', '2026-06-24 10:00:00'),
(5, 4, 1007, 85, 82, 88, 80, 92, '低GI主食，适合控糖。', '2026-06-24 10:00:00');

-- ========== 膳食计划 ==========
INSERT INTO meal_plan (id, user_id, plan_date, target_calorie, actual_calorie, score, recommend_reason, status) VALUES
(2001, 2, '2026-06-24', 1600, 1580.00, 86, '热量接近目标，晚餐蛋白质较充足。', 'ACTIVE');

INSERT INTO meal_plan_item (id, plan_id, meal_type, recipe_id, recipe_name, calorie, suitability_score, recommend_reason, sort_no) VALUES
(3001, 2001, 'BREAKFAST', 1003, '燕麦牛奶早餐', 320.00, 82, '早餐热量适中。', 1),
(3002, 2001, 'LUNCH', 1002, '番茄炒蛋', 280.00, 78, '午餐简单快捷。', 2),
(3003, 2001, 'DINNER', 1001, '鸡胸肉蔬菜碗', 360.00, 88, '高蛋白低脂晚餐。', 3),
(3004, 2001, 'SNACK', 1006, '苹果轻食', 104.00, 80, '低糖加餐。', 4);

INSERT INTO meal_plan_feedback (id, plan_id, plan_item_id, user_id, meal_type, feedback_date, status, actual_ratio, reason, remark, created_at) VALUES
(1, 2001, 3001, 2, 'BREAKFAST', '2026-06-24', 'COMPLETED', 1.00, NULL, '按计划完成', '2026-06-24 09:00:00'),
(2, 2001, 3002, 2, 'LUNCH', '2026-06-24', 'PARTIAL', 0.50, NULL, '只吃了一半', '2026-06-24 13:00:00'),
(3, 2001, 3003, 2, 'DINNER', '2026-06-24', 'REPLACED', 1.00, 'DISLIKE', '替换为清炒西兰花鸡蛋', '2026-06-24 19:00:00'),
(4, 2001, 3004, 2, 'SNACK', '2026-06-24', 'SKIPPED', 0.00, 'NO_TIME', '没时间吃', '2026-06-24 16:00:00');

INSERT INTO meal_plan_replace_log (id, plan_id, plan_item_id, user_id, old_recipe_id, new_recipe_id, replace_reason, calorie_delta, recommend_score, recommend_reason, created_at) VALUES
(1, 2001, 3003, 2, 1001, 1005, 'DISLIKE', -20.00, 92, '热量接近原计划，蛋白质更高，近期未重复。', '2026-06-24 18:30:00');

-- ========== 膳食记录 ==========
INSERT INTO meal_record (id, user_id, record_date, meal_type, source_type, source_id, total_calorie, total_protein, total_fat, total_carbohydrate, remark) VALUES
(4001, 2, '2026-06-24', 'BREAKFAST', 'PLAN', 3001, 320.00, 14.00, 10.00, 45.00, '计划执行'),
(4002, 2, '2026-06-24', 'LUNCH', 'PLAN', 3002, 140.00, 9.00, 10.00, 6.00, '部分完成'),
(4003, 2, '2026-06-24', 'DINNER', 'RECIPE', 1005, 340.00, 28.00, 18.00, 12.00, '替换后完成'),
(4004, 2, '2026-06-25', 'DINNER', 'MANUAL', NULL, 174.00, 3.90, 0.45, 38.25, '手动记录晚餐');

INSERT INTO meal_record_item (id, meal_record_id, food_name, amount_g, calorie, protein, fat, carbohydrate) VALUES
(1, 4004, '米饭', 150.00, 174.00, 3.90, 0.45, 38.25);

-- ========== 营养风险 ==========
INSERT INTO nutrition_risk_rule (id, rule_code, rule_name, scenario, nutrient, operator, threshold_min, threshold_max, severity, message, status) VALUES
(1, 'FAT_LOSS_CALORIE_GT_TARGET', '减脂热量超标预警', 'FAT_LOSS', 'CALORIE', 'GT', NULL, 1600.00, 'WARNING', '今日热量已超出减脂目标。', 'ENABLED'),
(2, 'SUGAR_CONTROL_CARB_GT_MEAL', '控糖餐碳水偏高', 'SUGAR_CONTROL', 'CARBOHYDRATE', 'GT', NULL, 90.00, 'WARNING', '当前餐碳水偏高，建议减少主食。', 'ENABLED'),
(3, 'HYPERTENSION_SODIUM_GT_DAY', '高血压高钠预警', 'HYPERTENSION', 'SODIUM', 'GT', NULL, 2000.00, 'DANGER', '钠摄入偏高，建议少盐。', 'ENABLED'),
(4, 'MUSCLE_GAIN_PROTEIN_LT_DAY', '增肌蛋白质不足', 'MUSCLE_GAIN', 'PROTEIN', 'LT', 120.00, NULL, 'INFO', '蛋白质不足，建议补充优质蛋白。', 'ENABLED');

INSERT INTO nutrition_risk_result (id, user_id, rule_id, source_type, source_id, risk_date, severity, message, suggestion, created_at) VALUES
(1, 2, 1, 'DAY_SUMMARY', NULL, '2026-06-24', 'INFO', '午餐碳水略高，可适当减少主食。', '晚餐减少油脂和主食。', '2026-06-24 20:00:00'),
(2, 2, 1, 'DAY_SUMMARY', NULL, '2026-06-25', 'WARNING', '今日热量已超出减脂目标。', '晚餐减少油脂和主食。', '2026-06-25 20:00:00');

INSERT INTO nutrition_report (id, user_id, report_type, start_date, end_date, avg_calorie, avg_protein, avg_fat, avg_carbohydrate, target_days, record_days, completion_rate, risk_count, summary, suggestions_json, created_at) VALUES
(1, 2, 'WEEKLY', '2026-06-22', '2026-06-28', 1520.00, 82.00, 42.00, 180.00, 5, 6, 78.50, 3, '本周整体热量控制较好，蛋白质摄入稳定。', '["继续保持早餐记录习惯。","注意控制晚餐碳水。"]', '2026-06-28 22:00:00');

-- ========== AI 解析菜谱 ==========
INSERT INTO ai_recipe (id, user_id, source_type, recipe_name, description, recognized_foods_json, ingredients_json, nutrition_json, suitability_score, suitability_reason, health_tips_json, warnings_json, raw_response_json, source_image_url, source_image_key, status, created_at) VALUES
(30001, 2, 'TEXT', '低脂鸡肉蔬菜碗', '适合减脂晚餐的高蛋白菜谱', NULL,
 '[{"name":"鸡胸肉","amount":120,"unit":"g"},{"name":"西兰花","amount":150,"unit":"g"}]',
 '{"calories":360,"protein":36,"fat":8,"carbohydrate":32}',
 88, '蛋白质充足，油脂较低，适合减脂晚餐。',
 '["建议少油烹饪"]', '[]', '{}', NULL, NULL, 'CONFIRMED', '2026-06-24 11:00:00'),
(30002, 2, 'IMAGE', 'AI识别晚餐', '图片解析生成的菜谱', '[{"name":"chicken breast","confidence":0.92}]',
 '[{"name":"鸡胸肉","amount":120,"unit":"g"}]',
 '{"calories":360,"protein":36,"fat":8,"carbohydrate":32}',
 85, '识别置信度较高，营养结构合理。', '[]', '[]', '{}',
 '/uploads/ai/30002.jpg', 'ai/30002.jpg', 'PARSED', '2026-06-25 12:00:00');

INSERT INTO ai_recipe_step (id, ai_recipe_id, step_no, content) VALUES
(1, 30001, 1, '鸡胸肉切片，加入少量盐和黑胡椒腌制。'),
(2, 30001, 2, '西兰花焯水后捞出。'),
(3, 30001, 3, '平底锅少油煎熟鸡胸肉。'),
(4, 30001, 4, '将鸡胸肉和蔬菜装盘。'),
(5, 30002, 1, '识别图片中的食材并准备烹饪。');

INSERT INTO ai_call_log (id, user_id, scene, request_summary, elapsed_ms, success, error_message, created_at) VALUES
(1, 2, 'TEXT_PARSE', '想做减脂晚餐，食材有鸡胸肉和西兰花', 1200, TRUE, NULL, '2026-06-24 11:00:00'),
(2, 2, 'IMAGE_PARSE', '上传食物图片解析', 3500, TRUE, NULL, '2026-06-25 12:00:00'),
(3, 2, 'TEXT_PARSE', 'DeepSeek API key is not configured', 50, FALSE, 'DeepSeek API key is not configured', '2026-06-25 14:00:00');

-- ========== 社区 ==========
INSERT INTO community_post (id, user_id, title, content, recipe_name, tags_json, source_type, source_id, status, like_count, comment_count, favorite_count, published_at, created_at) VALUES
(5001, 2, '低脂鸡胸便当分享', '今天做了一份适合工作日的高蛋白午餐。', '低脂鸡胸蔬菜卷', '["高蛋白","午餐"]', 'MANUAL', NULL, 'ONLINE', 18, 3, 6, '2026-06-25 10:30:00', '2026-06-25 10:00:00'),
(5002, 3, '增肌蛋白餐心得', '分享我的增肌午餐搭配。', '增肌蛋白餐', '["增肌","高蛋白"]', 'RECIPE', 1008, 'ONLINE', 12, 1, 4, '2026-06-26 09:00:00', '2026-06-26 08:30:00'),
(5003, 2, '减脂晚餐分享', 'AI解析后整理的晚餐菜谱。', '低脂鸡肉蔬菜碗', '["减脂","晚餐"]', 'AI_RECIPE', 30001, 'PENDING', 0, 0, 0, NULL, '2026-06-27 20:00:00');

INSERT INTO community_post_image (id, user_id, post_id, storage_key, image_url, sort_no, width, height, file_size, status, created_at) VALUES
(910001, 2, 5001, 'community/910001.webp', '/uploads/community/20260625/910001.webp', 1, 1280, 960, 582341, 'ACTIVE', '2026-06-25 09:50:00'),
(910002, 2, 5001, 'community/910002.webp', '/uploads/community/20260625/910002.webp', 2, 1280, 960, 498220, 'ACTIVE', '2026-06-25 09:55:00'),
(910003, 3, 5002, 'community/910003.webp', '/uploads/community/20260626/910003.webp', 1, 1280, 960, 512000, 'ACTIVE', '2026-06-26 08:00:00'),
(910004, 2, NULL, 'community/910004.webp', '/uploads/community/20260627/910004.webp', 0, 1280, 960, 450000, 'TEMP', '2026-06-27 19:50:00');

INSERT INTO community_comment (id, post_id, user_id, content, created_at) VALUES
(7001, 5001, 5, '这个搭配很适合工作日。', '2026-06-25 11:00:00'),
(7002, 5001, 3, '看起来不错，明天试试。', '2026-06-25 14:00:00'),
(7003, 5001, 4, '蛋白质比例很好。', '2026-06-25 16:00:00'),
(7004, 5002, 2, '增肌餐搭配学习了。', '2026-06-26 10:00:00');

INSERT INTO community_like (id, post_id, user_id, created_at) VALUES
(1, 5001, 3, '2026-06-25 11:30:00'),
(2, 5001, 4, '2026-06-25 12:00:00'),
(3, 5001, 5, '2026-06-25 13:00:00'),
(4, 5002, 2, '2026-06-26 09:30:00');

INSERT INTO community_favorite (id, post_id, user_id, created_at) VALUES
(1, 5001, 3, '2026-06-25 15:00:00'),
(2, 5001, 5, '2026-06-25 16:00:00'),
(3, 5002, 4, '2026-06-26 11:00:00');

-- ========== 系统基础 ==========
INSERT INTO notification (id, user_id, type, title, content, read_flag, related_type, related_id, created_at) VALUES
(1, 2, 'LIKE', '收到新点赞', 'Bob 赞了你的帖子「低脂鸡胸便当分享」', FALSE, 'POST', 5001, '2026-06-25 11:30:00'),
(2, 2, 'COMMENT', '收到新评论', 'Mika 评论了你的帖子', TRUE, 'POST', 5001, '2026-06-25 11:00:00'),
(3, 2, 'SYSTEM', '目标进度提醒', '你的减脂周期已完成 35%', FALSE, 'GOAL_CYCLE', 10, '2026-06-28 09:00:00');

INSERT INTO access_log (id, user_id, method, path, ip, elapsed_ms, status_code, biz_code, created_at) VALUES
(1, 2, 'POST', '/api/v1/auth/login', '127.0.0.1', 120, 200, 0, '2026-06-24 08:00:00'),
(2, 2, 'GET', '/api/v1/health-profile/me', '127.0.0.1', 45, 200, 0, '2026-06-24 08:01:00'),
(3, 2, 'POST', '/api/v1/meal-plans/generate/day', '127.0.0.1', 850, 200, 0, '2026-06-24 09:00:00'),
(4, 2, 'POST', '/api/v1/ai-recipes/parse', '127.0.0.1', 1200, 200, 0, '2026-06-24 11:00:00'),
(5, 1, 'GET', '/api/v1/admin/users', '127.0.0.1', 60, 200, 0, '2026-06-25 10:00:00');
