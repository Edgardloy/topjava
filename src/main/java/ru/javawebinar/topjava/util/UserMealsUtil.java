package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
        );
        List<UserMealWithExceed> filteredUsersWithFor = getFilteredWithExceededWithFor(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 500);

        System.out.println("<-- with for -->");
        for (UserMealWithExceed user : filteredUsersWithFor) {
            System.out.println(user.toString());
        }

        List<UserMealWithExceed> filteredUsersWithStream = getFilteredWithExceededWithStream(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 500);

        System.out.println("<-- with stream -->");
        for (UserMealWithExceed user : filteredUsersWithStream) {
            System.out.println(user.toString());
        }
//        .toLocalDate();
//        .toLocalTime();
    }

    private static List<UserMealWithExceed> getFilteredWithExceededWithFor(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> calForDate = new HashMap<>();
        for (UserMeal meal : mealList) {
            LocalDate mealDate = meal.getDateTime().toLocalDate();
            calForDate.put(mealDate, calForDate.getOrDefault(mealDate, 0) + meal.getCalories());
        }

        List<UserMealWithExceed> mealEx = new ArrayList<>();
        for (UserMeal meal : mealList) {
            LocalDateTime dateTime = meal.getDateTime();
            if (TimeUtil.isBetween(dateTime.toLocalTime(), startTime, endTime)) {
                mealEx.add(new UserMealWithExceed(dateTime, meal.getDescription(), meal.getCalories(),
                        calForDate.get(dateTime.toLocalDate()) > caloriesPerDay));
            }
        }
        return mealEx;
    }

    private static List<UserMealWithExceed> getFilteredWithExceededWithStream(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> calForDate = mealList.stream().collect(Collectors.groupingBy(um -> um.getDateTime().toLocalDate(),
                Collectors.summingInt(UserMeal::getCalories)));

        return mealList.stream()
                .filter(u -> TimeUtil.isBetween(u.getDateTime().toLocalTime(), startTime, endTime))
                .map(u -> new UserMealWithExceed(u.getDateTime(), u.getDescription(), u.getCalories(),
                        calForDate.get(u.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }
}
