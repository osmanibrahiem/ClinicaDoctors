package com.clinica.doctors.Models;

public class Consultation {

    private String id, question, answer, questionPublisherID, specializationID, status, answerPublisherID, image;
    private long questionDate, answerDate;

    public static final String STATUS_ACTIVE = "Active";

    public Consultation() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestionPublisherID() {
        return questionPublisherID;
    }

    public void setQuestionPublisherID(String questionPublisherID) {
        this.questionPublisherID = questionPublisherID;
    }

    public String getSpecializationID() {
        return specializationID;
    }

    public void setSpecializationID(String specializationID) {
        this.specializationID = specializationID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAnswerPublisherID() {
        return answerPublisherID;
    }

    public void setAnswerPublisherID(String answerPublisherID) {
        this.answerPublisherID = answerPublisherID;
    }

    public long getQuestionDate() {
        return questionDate;
    }

    public void setQuestionDate(long questionDate) {
        this.questionDate = questionDate;
    }

    public long getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(long answerDate) {
        this.answerDate = answerDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
