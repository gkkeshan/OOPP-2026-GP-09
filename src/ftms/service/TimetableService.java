package ftms.service;

import ftms.dao.BatchDAO;
import ftms.dao.TimetableDAO;
import ftms.dao.VenueDAO;
import ftms.exception.TimetableConflictException;
import ftms.model.Batch;
import ftms.model.Timetable;
import ftms.model.Venue;

import java.util.List;


public class TimetableService {

    private TimetableDAO timetableDAO = new TimetableDAO();
    private VenueDAO venueDAO = new VenueDAO();
    private BatchDAO batchDAO = new BatchDAO();


    public List<Timetable> getAllSessions() {
        return timetableDAO.findAll();
    }

    public List<Timetable> getSessionsByCourse(String courseCode) {
        return timetableDAO.findByCourse(courseCode);
    }

    public List<Timetable> getSessionsByBatch(int batchId) {
        return timetableDAO.findByBatch(batchId);
    }

    public List<Timetable> getSessionsByDay(String dayOfWeek) {
        return timetableDAO.findByDay(dayOfWeek);
    }

    public Timetable getSession(int timetableId) {
        return timetableDAO.findById(timetableId);
    }

    public Venue getVenue(int venueId) {
        return venueDAO.findById(venueId);
    }

    public List<Venue> getVenues() {
        return venueDAO.findAll();
    }

    public List<Batch> getBatches() {
        return batchDAO.findAll();
    }


    public void addSession(Timetable timetable) throws TimetableConflictException {
        validateTimes(timetable);
        checkVenueClash(timetable);
        timetableDAO.insert(timetable);
    }

    public void updateSession(Timetable timetable) throws TimetableConflictException {
        validateTimes(timetable);
        checkVenueClash(timetable);
        timetableDAO.update(timetable);
    }

    public void deleteSession(int timetableId) {
        timetableDAO.deleteById(timetableId);
    }


    private void validateTimes(Timetable timetable) {
        if (timetable.getStartTime() == null || timetable.getEndTime() == null) {
            throw new IllegalArgumentException("Start and end time are required");
        }
        if (!timetable.getEndTime().isAfter(timetable.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }


    private void checkVenueClash(Timetable timetable) throws TimetableConflictException {
        if (timetableDAO.hasVenueClash(timetable)) {
            throw new TimetableConflictException(
                    "Venue is already booked for an overlapping session on " + timetable.getDayOfWeek());
        }
    }
}