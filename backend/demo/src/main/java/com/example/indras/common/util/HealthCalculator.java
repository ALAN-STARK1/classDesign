package com.example.indras.common.util;

import com.example.indras.common.enums.ActivityLevel;
import com.example.indras.common.enums.Gender;
import com.example.indras.common.enums.HealthGoal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

public final class HealthCalculator {

    private HealthCalculator() {
    }

    public static BigDecimal calculateBmi(BigDecimal heightCm, BigDecimal weightKg) {
        if (heightCm == null || weightKg == null || heightCm.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal heightM = heightCm.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return weightKg.divide(heightM.multiply(heightM), 2, RoundingMode.HALF_UP);
    }

    public static String bmiStatus(BigDecimal bmi) {
        if (bmi == null) {
            return "未知";
        }
        double v = bmi.doubleValue();
        if (v < 18.5) {
            return "偏瘦";
        }
        if (v <= 23.9) {
            return "正常";
        }
        if (v <= 27.9) {
            return "超重";
        }
        return "肥胖";
    }

    public static int calculateAge(LocalDate birthday) {
        if (birthday == null) {
            return 30;
        }
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    public static int calculateBmr(Gender gender, BigDecimal heightCm, BigDecimal weightKg, LocalDate birthday) {
        int age = calculateAge(birthday);
        double weight = weightKg.doubleValue();
        double height = heightCm.doubleValue();
        double bmr = 10 * weight + 6.25 * height - 5 * age;
        if (gender == Gender.MALE) {
            bmr += 5;
        } else {
            bmr -= 161;
        }
        return (int) Math.round(Math.max(bmr, 800));
    }

    public static double activityFactor(ActivityLevel level) {
        if (level == null) {
            return 1.375;
        }
        return switch (level) {
            case SEDENTARY -> 1.2;
            case LIGHT -> 1.375;
            case MODERATE -> 1.55;
            case HIGH -> 1.725;
        };
    }

    public static ActivityLevel parseActivityLevel(String value) {
        if (value == null || value.isBlank()) {
            return ActivityLevel.LIGHT;
        }
        return ActivityLevel.valueOf(value);
    }

    public static Gender parseGender(String value) {
        if (value == null || value.isBlank()) {
            return Gender.UNKNOWN;
        }
        return Gender.valueOf(value);
    }

    public static HealthGoal parseHealthGoal(String value) {
        if (value == null || value.isBlank()) {
            return HealthGoal.MAINTAIN;
        }
        return HealthGoal.valueOf(value);
    }

    public static int calculateTdee(int bmr, ActivityLevel activityLevel) {
        return (int) Math.round(bmr * activityFactor(activityLevel));
    }

    public static int calculateDailyCalorieTarget(int tdee, HealthGoal goal) {
        if (goal == null) {
            return tdee;
        }
        return switch (goal) {
            case FAT_LOSS -> tdee - 300;
            case MUSCLE_GAIN -> tdee + 300;
            case SUGAR_CONTROL -> tdee - 100;
            case MAINTAIN -> tdee;
        };
    }

    public static BigDecimal weeklyTargetDelta(BigDecimal startWeight, BigDecimal targetWeight, LocalDate start, LocalDate end) {
        long weeks = Math.max(Period.between(start, end).getDays() / 7L, 1L);
        return targetWeight.subtract(startWeight)
                .divide(BigDecimal.valueOf(weeks), 2, RoundingMode.HALF_UP);
    }
}
