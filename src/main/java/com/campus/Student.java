package com.campus;

/**
 * Student model — mirrors the students table.
 */
public class Student {

    private String studentId;
    private String fullName;
    private String course;
    private String yearLevel;
    private String contact;
    private String email;

    public Student() {}

    public Student(String studentId, String fullName,
                   String course, String yearLevel,
                   String contact, String email) {
        this.studentId = studentId;
        this.fullName  = fullName;
        this.course    = course;
        this.yearLevel = yearLevel;
        this.contact   = contact;
        this.email     = email;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public String getStudentId()              { return studentId; }
    public void   setStudentId(String v)      { this.studentId = v; }

    public String getFullName()               { return fullName; }
    public void   setFullName(String v)       { this.fullName = v; }

    public String getCourse()                 { return course; }
    public void   setCourse(String v)         { this.course = v; }

    public String getYearLevel()              { return yearLevel; }
    public void   setYearLevel(String v)      { this.yearLevel = v; }

    public String getContact()                { return contact; }
    public void   setContact(String v)        { this.contact = v; }

    public String getEmail()                  { return email; }
    public void   setEmail(String v)          { this.email = v; }

    @Override
    public String toString() {
        return studentId + " – " + fullName;
    }
}
