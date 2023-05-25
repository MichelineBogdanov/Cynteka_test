package org.example;

/*
 * Необходимо написать консольное приложение на Java(главный класс называть Main), в которое читает из файла input.txt входные данные:
 * n - число, далее n строк, m - число, далее m строк
 *
 * Пример 1:
 * input.txt:
 * 4
 * гвоздь
 * шуруп
 * краска синяя
 * ведро для воды
 * 3
 * краска
 * корыто для воды
 * шуруп 3х1.5
 * ouput.txt:
 * гвоздь:?
 * шуруп:шуруп 3х1.5
 * краска синяя:краска
 * ведро для воды:корыто для воды
 *
 * Пример 2:
 * 1
 * Бетон с присадкой
 * 1
 * Цемент
 * ouput.txt:
 * Бетон с присадкой:Цемент
 *
 * Пример 3:
 * 1
 * Бетон с присадкой
 * 2
 * присадка для бетона
 * доставка
 * ouput.txt:
 * Бетон с присадкой:присадка бля бетона
 * доставка:?
 * Программа должна сопоставить максимально похожие строки из первого множества со строками из второго множества (одна к одной) и вывести результат в файл output.txt.
 * */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        Main main = new Main();

        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\qqMik\\Documents\\test.txt"))) {
            int n = Integer.parseInt(reader.readLine());
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                firstList.add(reader.readLine());
            }
            int m = Integer.parseInt(reader.readLine());
            List<String> secondList = new ArrayList<>();
            for (int i = 0; i < m; i++) {
                secondList.add(reader.readLine());
            }
            Map<String, String> resultMap = main.getResultMap(firstList, secondList);
            for (Map.Entry<String, String> stringStringEntry : resultMap.entrySet()) {
                System.out.println(stringStringEntry.getKey() + " " + stringStringEntry.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // Поиск наиболее похожих строк между списками
    private Map<String, String> getResultMap(List<String> firstList, List<String> secondList) {
        Map<String, String> resultMap = new HashMap<>();
        Iterator<String> iterator = firstList.iterator();
        List<Double> similarityCoefficientList = new ArrayList<>();
        while (iterator.hasNext()) {
            String sourceString = iterator.next();
            List<Double> wordSimilarityCoefficientList = findWordSimilarityCoefficientList(sourceString, secondList);
            similarityCoefficientList.addAll(wordSimilarityCoefficientList);
        }
        int firstSize = firstList.size();
        int secondSize = secondList.size();
        for (int i = 0; i < firstSize; i++) {
            List<Double> words = new ArrayList<>();
            for (int j = 0; j < secondSize; j++) {
                words.add(similarityCoefficientList.get(j * secondSize + i));
            }
            Optional<Double> max = words.stream().max(Comparator.comparingDouble(a -> a));
            int index = similarityCoefficientList.indexOf(max.get());
            int firstWord = index / secondSize;
            int secondWord = index % secondSize;
            System.out.println(firstWord + " " + secondWord);
            resultMap.put(firstList.get(firstWord), secondList.get(secondWord));
        }
        System.out.println(resultMap);
        return resultMap;
    }

    // Расчет коэффициента подобия и возврат листа коэффициентов подобия слова/словосочетания 1 списка ко всему второму
    public List<Double> findWordSimilarityCoefficientList(String dataString, List<String> checkList) {
        List<Double> result = new ArrayList<>();
        for (String checkString : checkList) {
            boolean[][] collisions = new boolean[dataString.length()][checkString.length()];
            for (int i = 0; i < collisions.length; i++) {
                for (int j = 0; j < collisions[i].length; j++) {
                    if (dataString.charAt(i) == checkString.charAt(j)) {
                        collisions[i][j] = true;
                    }
                }
            }
            List<Boolean> listOfCollisions = getListOfCollisions(collisions);
            List<Integer> countList = new ArrayList<>();
            Boolean reduce = listOfCollisions.stream()
                    .reduce(false, (prev, curr) -> {
                        if (curr) {
                            if (prev) {
                                int index = countList.size() - 1;
                                countList.set(index, countList.get(index) + 1);
                            } else {
                                countList.add(1);
                            }
                        }
                        return curr;
                    });
            int countSum = countList.stream()
                    .filter(count -> count > 1)
                    .mapToInt(count -> count * count)
                    .sum();
            int wordLength = dataString.length() * dataString.length();
            double collisionCoefficient = 0.9 * Math.pow((double) countSum / wordLength, 1.0 / 2);
            result.add(collisionCoefficient);
        }
        return result;
    }

    //получение списка значений пересечений по диагоналям
    private List<Boolean> getListOfCollisions(boolean[][] dataList) {
        List<Boolean> diagonals = new ArrayList<>();
        int row = dataList.length - 1;
        int column = 0;
        while (row > -1 && column < dataList[0].length) {
            int diagRow = row;
            int diagColumn = column;
            while (diagRow < dataList.length && diagColumn < dataList[0].length) {
                diagonals.add(dataList[diagRow][diagColumn]);
                diagRow++;
                diagColumn++;
            }
            if (row == 0 && diagColumn <= dataList[0].length) {
                column++;
            }
            if (column == 0) {
                row--;
            }
        }
        return diagonals;
    }

}
