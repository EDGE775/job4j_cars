package ru.job4j.cars.service.filter;

import ru.job4j.cars.model.Announcement;
import ru.job4j.cars.service.HbmService;

import java.util.List;

public class AnnoFactory {
    private static AnnoFactory annoFactory;

    private AnnoFactory() {
    }

    public static AnnoFactory getFactory() {
        if (annoFactory == null) {
            annoFactory = new AnnoFactory();
        }
        return annoFactory;
    }

    public List<Announcement> getAnnosByFilter(Filter filter) {
        switch (filter) {
            case FIND_ALL:
                return HbmService.instOf().findAllAnnouncements();
            case FIND_TODAY:
                return HbmService.instOf().findAllAnnouncementsPerLastDay();
            case FIND_WITH_PHOTO:
                return HbmService.instOf().findAllAnnouncementsWithImages();
            default:
                throw new UnsupportedOperationException(String.format("Filter '%s' not found.", filter));
        }
    }
}