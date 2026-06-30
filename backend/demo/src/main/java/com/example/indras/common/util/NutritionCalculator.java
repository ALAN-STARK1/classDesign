package com.example.indras.common.util;

import com.example.indras.ingredient.entity.Ingredient;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class NutritionCalculator {

    private NutritionCalculator() {
    }

    public static BigDecimal scalePer100g(BigDecimal per100g, BigDecimal amountG) {
        if (per100g == null || amountG == null) {
            return BigDecimal.ZERO;
        }
        return per100g.multiply(amountG).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public static Nutrients fromIngredient(Ingredient ingredient, BigDecimal amountG) {
        return new Nutrients(
                scalePer100g(ingredient.getCalorie(), amountG),
                scalePer100g(ingredient.getProtein(), amountG),
                scalePer100g(ingredient.getFat(), amountG),
                scalePer100g(ingredient.getCarbohydrate(), amountG),
                scalePer100g(ingredient.getSodium(), amountG)
        );
    }

    public static Nutrients add(Nutrients a, Nutrients b) {
        return new Nutrients(
                a.calorie().add(b.calorie()),
                a.protein().add(b.protein()),
                a.fat().add(b.fat()),
                a.carbohydrate().add(b.carbohydrate()),
                a.sodium().add(b.sodium())
        );
    }

    public static Nutrients scale(Nutrients nutrients, BigDecimal ratio) {
        if (ratio == null) {
            ratio = BigDecimal.ONE;
        }
        return new Nutrients(
                nutrients.calorie().multiply(ratio).setScale(2, RoundingMode.HALF_UP),
                nutrients.protein().multiply(ratio).setScale(2, RoundingMode.HALF_UP),
                nutrients.fat().multiply(ratio).setScale(2, RoundingMode.HALF_UP),
                nutrients.carbohydrate().multiply(ratio).setScale(2, RoundingMode.HALF_UP),
                nutrients.sodium().multiply(ratio).setScale(2, RoundingMode.HALF_UP)
        );
    }

    public record Nutrients(
            BigDecimal calorie,
            BigDecimal protein,
            BigDecimal fat,
            BigDecimal carbohydrate,
            BigDecimal sodium
    ) {
        public Nutrients {
            calorie = calorie == null ? BigDecimal.ZERO : calorie;
            protein = protein == null ? BigDecimal.ZERO : protein;
            fat = fat == null ? BigDecimal.ZERO : fat;
            carbohydrate = carbohydrate == null ? BigDecimal.ZERO : carbohydrate;
            sodium = sodium == null ? BigDecimal.ZERO : sodium;
        }

        public static Nutrients zero() {
            return new Nutrients(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
}
