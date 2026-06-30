"""Generate MyBatis-Plus mapper interfaces for each entity."""
from __future__ import annotations

from pathlib import Path

BASE = Path(__file__).resolve().parents[1]
ENTITY_GLOB = "src/main/java/com/example/indras/**/entity/*.java"


def mapper_content(module: str, entity_name: str) -> str:
    return f"""package com.example.indras.{module}.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.indras.{module}.entity.{entity_name};
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface {entity_name}Mapper extends BaseMapper<{entity_name}> {{
}}
"""


def main() -> None:
    created: list[Path] = []
    for entity_path in sorted(BASE.glob(ENTITY_GLOB)):
        entity_name = entity_path.stem
        # .../indras/{module}/entity/{Entity}.java
        module = entity_path.parent.parent.name
        mapper_dir = BASE / f"src/main/java/com/example/indras/{module}/mapper"
        mapper_dir.mkdir(parents=True, exist_ok=True)
        mapper_path = mapper_dir / f"{entity_name}Mapper.java"
        content = mapper_content(module, entity_name)
        if not mapper_path.exists() or mapper_path.read_text(encoding="utf-8") != content:
            mapper_path.write_text(content, encoding="utf-8")
            created.append(mapper_path)
            print(f"Created {mapper_path.relative_to(BASE)}")
        else:
            print(f"Skipped (unchanged) {mapper_path.relative_to(BASE)}")

    print(f"Total created/updated: {len(created)}")


if __name__ == "__main__":
    main()
