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

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Приветствую! Введите полный путь файла источника:");
        String sourceFile = scanner.nextLine();
        System.out.println("Введите полный путь файла источника:");
        String destFile = scanner.nextLine();
        main.run(sourceFile, destFile);
        System.out.println("Готово!");
    }

    //чтение и запись результата
    private void run(String sourceFilePath, String destFileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(destFileName))) {
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
            Map<String, String> resultMap = getResultMap(firstList, secondList);
            if (firstList.size() >= secondList.size()) {
                for (String first : firstList) {
                    if (resultMap.containsKey(first)) {
                        writer.write(first + ":" + resultMap.get(first) + "\n");
                    } else {
                        writer.write(first + ":?" + "\n");
                    }
                }
            } else {
                for (String second : secondList) {
                    if (resultMap.containsKey(second)) {
                        writer.write(second + ":" + resultMap.get(second) + "\n");
                    } else {
                        writer.write("?:" + second + "\n");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Поиск наиболее похожих строк между списками
    private Map<String, String> getResultMap(List<String> firstList, List<String> secondList) {
        HashMap<String, String> resultMap = new HashMap<>();
        boolean swap = false;
        if (firstList.size() > secondList.size()) {
            List<String> buffer = new ArrayList<>(firstList);
            firstList.clear();
            firstList.addAll(secondList);
            secondList.clear();
            secondList.addAll(buffer);
            swap = true;
        }
        for (String sourceString : firstList) {
            String findWord = findWord(sourceString, secondList);
            if (!swap) {
                resultMap.put(findWord, sourceString);
            } else {
                resultMap.put(sourceString, findWord);
            }
        }
        return resultMap;
    }

    // Расчет коэффициента подобия и возврат наиболее подходящей строки
    private String findWord(String dataString, List<String> checkList) {
        Map<String, Double> resultMap = new HashMap<>();
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
            resultMap.put(checkString, collisionCoefficient);
        }
        Optional<Map.Entry<String, Double>> max = resultMap.entrySet().stream().max(Comparator.comparingDouble(Map.Entry::getValue));
        return max.get().getKey();
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
