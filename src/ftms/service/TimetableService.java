package ftms.service;

import ftms.dao.TimetableDAO;
import ftms.dao.VenueDAO;
import ftms.exception.TimetableConflictException;
import ftms.model.Timetable;
import ftms.model.Venue;

import java.time.LocalDate;
import java.util.List;

public class TimetableService {

    private TimetableDAO timetableDAO = new TimetableDAO();
    private VenueDAO venueDAO = new VenueDAO();

    public List<Timetable> getAllSessions() {
        return timetableDAO.findAll();
    }

    public List<Timetable> getSessionsByCourse(String courseCode) {
        return timetableDAO.findByCourse(courseCode);
    }

    public List<Timetable> getSessionsByDate(LocalDate date) {
        return timetableDAO.findByDate(date);
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

    public void addSession(Timetable session) throws TimetableConflictException {
        validateTimes(session);
        checkVenueClash(session);
        timetableDAO.insert(session);
    }

    public void updateSession(Timetable session) throws TimetableConflictException {
        validateTimes(session);
        checkVenueClash(session);
        timetableDAO.update(session);
    }

    public void deleteSession(int timetableId) {
        timetableDAO.deleteById(timetableId);
    }

    private void checkVenueClash(Timetable session) throws TimetableConflictException {
        if (timetableDAO.hasVenueClash(session)) {
            throw new TimetableConflictException("Venue clash: this venue is already booked on "
                    + session.getSessionDate() + " from " + session.getStartTime() + " to " + session.getEndTime());
        }
    }

    private void validateTimes(Timetable session) {
        if (!session.getStartTime().isBefore(session.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }
}