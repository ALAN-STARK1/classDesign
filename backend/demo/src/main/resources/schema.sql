-- 智能健康膳食管理系统 H2 数据库表结构
-- 对应《智能健康膳食管理系统业务后端设计文档-H2版》第 9 章
-- H2 使用 MODE=MySQL，大文本字段统一用 LONGTEXT（勿用 CLOB）

CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    email           VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(20)  NOT NULL DEFAULT 'USER',
    status          VARCHAR(20)  NOT NULL DEFAULT 'ENABLED',
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_profile (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL UNIQUE,
    nickname        VARCHAR(50),
    avatar_url      VARCHAR(500),
    bio             VARCHAR(200),
    gender          VARCHAR(20),
    birthday        DATE,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS health_profile (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT        NOT NULL UNIQUE,
    gender               VARCHAR(20),
    birthday             DATE,
    height_cm            DECIMAL(5,2),
    weight_kg            DECIMAL(5,2),
    target_weight_kg     DECIMAL(5,2),
    activity_level       VARCHAR(30),
    health_goal          VARCHAR(30),
    bmi                  DECIMAL(5,2),
    daily_calorie_target INT
);

CREATE TABLE IF NOT EXISTS user_preference (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT       NOT NULL UNIQUE,
    liked_tags           VARCHAR(1000),
    disliked_ingredients VARCHAR(1000),
    preferred_cuisines   VARCHAR(500),
    spice_level          VARCHAR(20)  DEFAULT 'MEDIUM',
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_allergen (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    allergen_name   VARCHAR(100) NOT NULL,
    UNIQUE (user_id, allergen_name)
);

CREATE TABLE IF NOT EXISTS user_diet_restriction (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT       NOT NULL,
    restriction_name  VARCHAR(100) NOT NULL,
    UNIQUE (user_id, restriction_name)
);

CREATE TABLE IF NOT EXISTS weight_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    record_date     DATE          NOT NULL,
    weight_kg       DECIMAL(5,2)  NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS health_goal_cycle (
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                  BIGINT        NOT NULL,
    goal_type                VARCHAR(30)   NOT NULL,
    start_date               DATE          NOT NULL,
    end_date                 DATE          NOT NULL,
    start_weight_kg          DECIMAL(5,2)  NOT NULL,
    target_weight_kg         DECIMAL(5,2)  NOT NULL,
    target_calorie           INT           NOT NULL,
    weekly_target_delta_kg   DECIMAL(4,2),
    progress_percent         DECIMAL(5,2)  DEFAULT 0,
    status                   VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    created_at               TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ingredient (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    category        VARCHAR(50)  NOT NULL,
    unit            VARCHAR(20)  NOT NULL DEFAULT 'g',
    calorie         DECIMAL(8,2) NOT NULL DEFAULT 0,
    protein         DECIMAL(8,2) NOT NULL DEFAULT 0,
    fat             DECIMAL(8,2) NOT NULL DEFAULT 0,
    carbohydrate    DECIMAL(8,2) NOT NULL DEFAULT 0,
    sodium          DECIMAL(8,2) NOT NULL DEFAULT 0,
    status          VARCHAR(20)  NOT NULL DEFAULT 'ENABLED'
);

CREATE TABLE IF NOT EXISTS recipe (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT,
    name                 VARCHAR(100)  NOT NULL,
    description          VARCHAR(1000),
    category             VARCHAR(30)   NOT NULL,
    difficulty           VARCHAR(20)   NOT NULL DEFAULT 'NORMAL',
    cook_minutes         INT           NOT NULL DEFAULT 0,
    servings             INT           NOT NULL DEFAULT 1,
    total_calorie        DECIMAL(8,2)  NOT NULL DEFAULT 0,
    total_protein        DECIMAL(8,2)  NOT NULL DEFAULT 0,
    total_fat            DECIMAL(8,2)  NOT NULL DEFAULT 0,
    total_carbohydrate   DECIMAL(8,2)  NOT NULL DEFAULT 0,
    status               VARCHAR(20)   NOT NULL DEFAULT 'DRAFT'
);

CREATE TABLE IF NOT EXISTS recipe_ingredient (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id       BIGINT        NOT NULL,
    ingredient_id   BIGINT        NOT NULL,
    amount_g        DECIMAL(8,2)  NOT NULL,
    remark          VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS recipe_step (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id       BIGINT        NOT NULL,
    step_no         INT           NOT NULL,
    content         VARCHAR(1000) NOT NULL
);

CREATE TABLE IF NOT EXISTS recipe_favorite (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT    NOT NULL,
    recipe_id       BIGINT    NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, recipe_id)
);

CREATE TABLE IF NOT EXISTS recipe_suitability_score (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT        NOT NULL,
    recipe_id           BIGINT        NOT NULL,
    score               INT           NOT NULL DEFAULT 0,
    calorie_score       INT           NOT NULL DEFAULT 0,
    macro_score         INT           NOT NULL DEFAULT 0,
    preference_score    INT           NOT NULL DEFAULT 0,
    risk_score          INT           NOT NULL DEFAULT 0,
    reason              VARCHAR(1000),
    calculated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, recipe_id)
);

CREATE TABLE IF NOT EXISTS meal_plan (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT        NOT NULL,
    plan_date        DATE          NOT NULL,
    target_calorie   INT           NOT NULL,
    actual_calorie   DECIMAL(8,2)  NOT NULL DEFAULT 0,
    score            INT           NOT NULL DEFAULT 0,
    recommend_reason VARCHAR(1000),
    status           VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    UNIQUE (user_id, plan_date)
);

CREATE TABLE IF NOT EXISTS meal_plan_item (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id             BIGINT        NOT NULL,
    meal_type           VARCHAR(20)   NOT NULL,
    recipe_id           BIGINT        NOT NULL,
    recipe_name         VARCHAR(100)  NOT NULL,
    calorie             DECIMAL(8,2)  NOT NULL DEFAULT 0,
    suitability_score   INT           NOT NULL DEFAULT 0,
    recommend_reason    VARCHAR(500),
    sort_no             INT           NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS meal_plan_feedback (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id         BIGINT        NOT NULL,
    plan_item_id    BIGINT        NOT NULL,
    user_id         BIGINT        NOT NULL,
    meal_type       VARCHAR(20)   NOT NULL,
    feedback_date   DATE          NOT NULL,
    status          VARCHAR(20)   NOT NULL,
    actual_ratio    DECIMAL(4,2)  DEFAULT 1.00,
    reason          VARCHAR(50),
    remark          VARCHAR(500),
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS meal_plan_replace_log (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id             BIGINT        NOT NULL,
    plan_item_id        BIGINT        NOT NULL,
    user_id             BIGINT        NOT NULL,
    old_recipe_id       BIGINT        NOT NULL,
    new_recipe_id       BIGINT        NOT NULL,
    replace_reason      VARCHAR(50)   NOT NULL,
    calorie_delta       DECIMAL(8,2)  DEFAULT 0,
    recommend_score     INT           DEFAULT 0,
    recommend_reason    VARCHAR(1000),
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS meal_record (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT        NOT NULL,
    record_date          DATE          NOT NULL,
    meal_type            VARCHAR(20)   NOT NULL,
    source_type          VARCHAR(30)   NOT NULL DEFAULT 'MANUAL',
    source_id            BIGINT,
    total_calorie        DECIMAL(8,2)  NOT NULL DEFAULT 0,
    total_protein        DECIMAL(8,2)  NOT NULL DEFAULT 0,
    total_fat            DECIMAL(8,2)  NOT NULL DEFAULT 0,
    total_carbohydrate   DECIMAL(8,2)  NOT NULL DEFAULT 0,
    remark               VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS meal_record_item (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    meal_record_id  BIGINT        NOT NULL,
    food_name       VARCHAR(100)  NOT NULL,
    amount_g        DECIMAL(8,2)  NOT NULL DEFAULT 0,
    calorie         DECIMAL(8,2)  NOT NULL DEFAULT 0,
    protein         DECIMAL(8,2)  NOT NULL DEFAULT 0,
    fat             DECIMAL(8,2)  NOT NULL DEFAULT 0,
    carbohydrate    DECIMAL(8,2)  NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS nutrition_risk_rule (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_code       VARCHAR(50)   NOT NULL UNIQUE,
    rule_name       VARCHAR(100)  NOT NULL,
    scenario        VARCHAR(30)   NOT NULL,
    nutrient        VARCHAR(30)   NOT NULL,
    operator        VARCHAR(10)   NOT NULL,
    threshold_min   DECIMAL(8,2),
    threshold_max   DECIMAL(8,2),
    severity        VARCHAR(20)   NOT NULL,
    message         VARCHAR(500)  NOT NULL,
    status          VARCHAR(20)   NOT NULL DEFAULT 'ENABLED'
);

CREATE TABLE IF NOT EXISTS nutrition_risk_result (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    rule_id         BIGINT        NOT NULL,
    source_type     VARCHAR(30)   NOT NULL,
    source_id       BIGINT,
    risk_date       DATE          NOT NULL,
    severity        VARCHAR(20)   NOT NULL,
    message         VARCHAR(500)  NOT NULL,
    suggestion      VARCHAR(500),
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS nutrition_report (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT        NOT NULL,
    report_type          VARCHAR(20)   NOT NULL,
    start_date           DATE          NOT NULL,
    end_date             DATE          NOT NULL,
    avg_calorie          DECIMAL(8,2)  NOT NULL DEFAULT 0,
    avg_protein          DECIMAL(8,2)  NOT NULL DEFAULT 0,
    avg_fat              DECIMAL(8,2)  NOT NULL DEFAULT 0,
    avg_carbohydrate     DECIMAL(8,2)  NOT NULL DEFAULT 0,
    target_days          INT           NOT NULL DEFAULT 0,
    record_days          INT           NOT NULL DEFAULT 0,
    completion_rate      DECIMAL(5,2)  NOT NULL DEFAULT 0,
    risk_count           INT           NOT NULL DEFAULT 0,
    summary              VARCHAR(1000),
    suggestions_json     LONGTEXT,
    created_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ai_recipe (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                 BIGINT        NOT NULL,
    source_type             VARCHAR(20)   NOT NULL,
    recipe_name             VARCHAR(100)  NOT NULL,
    description             VARCHAR(1000),
    recognized_foods_json   LONGTEXT,
    ingredients_json        LONGTEXT,
    nutrition_json          LONGTEXT,
    suitability_score       INT           DEFAULT 0,
    suitability_reason      VARCHAR(1000),
    health_tips_json        LONGTEXT,
    warnings_json           LONGTEXT,
    raw_response_json       LONGTEXT,
    source_image_url        VARCHAR(500),
    source_image_key        VARCHAR(200),
    status                  VARCHAR(20)   NOT NULL DEFAULT 'PARSED',
    created_at              TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ai_recipe_step (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    ai_recipe_id    BIGINT        NOT NULL,
    step_no         INT           NOT NULL,
    content         VARCHAR(1000) NOT NULL
);

CREATE TABLE IF NOT EXISTS ai_call_log (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT        NOT NULL,
    scene               VARCHAR(30)   NOT NULL,
    request_summary     VARCHAR(500),
    elapsed_ms          INT           NOT NULL DEFAULT 0,
    success             BOOLEAN       NOT NULL DEFAULT TRUE,
    error_message       VARCHAR(500),
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS community_post (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    title           VARCHAR(100)  NOT NULL,
    content         LONGTEXT          NOT NULL,
    recipe_name     VARCHAR(100),
    tags_json       VARCHAR(500),
    source_type     VARCHAR(30)   NOT NULL DEFAULT 'MANUAL',
    source_id       BIGINT,
    status          VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    like_count      INT           NOT NULL DEFAULT 0,
    comment_count   INT           NOT NULL DEFAULT 0,
    favorite_count  INT           NOT NULL DEFAULT 0,
    published_at    TIMESTAMP,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS community_post_image (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    post_id         BIGINT,
    storage_key     VARCHAR(200)  NOT NULL,
    image_url       VARCHAR(500)  NOT NULL,
    sort_no         INT           NOT NULL DEFAULT 0,
    width           INT,
    height          INT,
    file_size       BIGINT,
    status          VARCHAR(20)   NOT NULL DEFAULT 'TEMP',
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS community_comment (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id         BIGINT        NOT NULL,
    user_id         BIGINT        NOT NULL,
    content         VARCHAR(1000) NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS community_like (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id         BIGINT    NOT NULL,
    user_id         BIGINT    NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (post_id, user_id)
);

CREATE TABLE IF NOT EXISTS community_favorite (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id         BIGINT    NOT NULL,
    user_id         BIGINT    NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (post_id, user_id)
);

CREATE TABLE IF NOT EXISTS notification (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    type            VARCHAR(30)   NOT NULL,
    title           VARCHAR(100)  NOT NULL,
    content         VARCHAR(500),
    read_flag       BOOLEAN       NOT NULL DEFAULT FALSE,
    related_type    VARCHAR(30),
    related_id      BIGINT,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS access_log (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT,
    method          VARCHAR(10)   NOT NULL,
    path            VARCHAR(255)  NOT NULL,
    ip              VARCHAR(50),
    elapsed_ms      INT           NOT NULL DEFAULT 0,
    status_code     INT           NOT NULL DEFAULT 200,
    biz_code        INT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
