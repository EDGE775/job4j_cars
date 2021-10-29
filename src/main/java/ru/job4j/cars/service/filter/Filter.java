package ru.job4j.cars.service.filter;

public enum Filter {
    FIND_ALL("Показать все", 1),
    FIND_TODAY("Показать сегодняшние", 2),
    FIND_WITH_PHOTO("Показать с фото", 3);

    private String title;

    private Integer number;

    Filter(String title, Integer number) {
        this.title = title;
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public Integer getNumber() {
        return number;
    }
}
