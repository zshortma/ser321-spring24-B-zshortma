package example.grpcclient;

import io.grpc.stub.StreamObserver;
import service.*;
import com.google.protobuf.Empty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class QandaImpl extends QandaGrpc.QandaImplBase {
    
    private final Map<Integer, Question> questions = new HashMap<>();
    private final Map<Integer, List<String>> answers = new HashMap<>();
    private int nextQuestionId = 1;

    @Override
    public void addQuestion(QuestionReq req, StreamObserver<QuestionRes> responseObserver) {
        Question question = req.getQuestion();
        int questionId = nextQuestionId++;
        
        question = question.toBuilder()
                .setId(questionId)
                .build();
        
        questions.put(questionId, question);

        // Create the response
        QuestionRes response = QuestionRes.newBuilder()
                .setIsSuccess(true)
                .setMessage("Question added successfully with ID: " + questionId)
                .build();

        // Send the response to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    
    @Override
    public void addAnswer(AnswerReq req, StreamObserver<AnswerRes> responseObserver) {
        int questionId = req.getId();
        String answer = req.getAnswer();

        if (questions.containsKey(questionId)) {
            // Add the answer to the question's list of answers
            answers.computeIfAbsent(questionId, k -> new ArrayList<>()).add(answer);

            // Create the response
            AnswerRes response = AnswerRes.newBuilder()
                    .setIsSuccess(true)
                    .build();

            // Send the response to the client
            responseObserver.onNext(response);
        } else {
            // Create an error response if the question ID does not exist
            AnswerRes response = AnswerRes.newBuilder()
                    .setIsSuccess(false)
                    .setError("Question with ID " + questionId + " does not exist.")
                    .build();

            // Send the response to the client
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    
    @Override
    public void viewQuestions(Empty req, StreamObserver<AllQuestionsRes> responseObserver) {
        AllQuestionsRes.Builder responseBuilder = AllQuestionsRes.newBuilder();

        //Go through question and add to map
        for (Map.Entry<Integer, Question> entry : questions.entrySet()) {
            Question question = entry.getValue();

            question = question.toBuilder()
                    .setId(entry.getKey())
                    .addAllAnswers(answers.getOrDefault(entry.getKey(), new ArrayList<>()))
                    .build();
            responseBuilder.addQuestions(question);
        }

        // Build the response and send it to the client
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
