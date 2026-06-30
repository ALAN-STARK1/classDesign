"""Convert JPA entity annotations to MyBatis-Plus."""
from __future__ import annotations

import re
from pathlib import Path

BASE = Path(__file__).resolve().parents[1]
ENTITY_GLOB = "src/main/java/com/example/indras/**/entity/*.java"

MP_IMPORTS = """import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;"""


def convert_entity(content: str) -> str:
    content = re.sub(r"import jakarta\.persistence\.\*;\n", "", content)
    content = re.sub(r"@Entity\n", "", content)
    content = re.sub(
        r'@Table\(name = "([^"]+)"\)',
        r'@TableName("\1")',
        content,
    )
    content = re.sub(
        r"@Id\s*\n\s*@GeneratedValue\(strategy = GenerationType\.IDENTITY\)",
        "@TableId(type = IdType.AUTO)",
        content,
    )
    content = re.sub(
        r'@Column\(name = "([^"]+)"\)\s*\n\s*',
        r'@TableField("\1")\n    ',
        content,
    )
    content = re.sub(r"@Lob\s*\n\s*", "", content)

    if "com.baomidou.mybatisplus.annotation.TableName" not in content:
        content = re.sub(
            r"(package [^;]+;\n\n)",
            rf"\1{MP_IMPORTS}\n",
            content,
            count=1,
        )

    return content


def main() -> None:
    modified: list[Path] = []
    for path in sorted(BASE.glob(ENTITY_GLOB)):
        original = path.read_text(encoding="utf-8")
        converted = convert_entity(original)
        if converted != original:
            path.write_text(converted, encoding="utf-8")
            modified.append(path)
            print(f"Modified {path.relative_to(BASE)}")
        else:
            print(f"Skipped (already converted) {path.relative_to(BASE)}")

    print(f"Total modified: {len(modified)}")


if __name__ == "__main__":
    main()
