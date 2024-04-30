package example.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerMethodDefinition;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import service.*;
import java.util.Stack;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;
import com.google.protobuf.ProtocolStringList;


class FollowImpl extends FollowGrpc.FollowImplBase {
    
    private final Map<String, String> users = new HashMap<>();
    private final Map<String, String> following = new HashMap<>();

public FollowImpl(){
    super();
  //  questions.add("Whats my dogs name?");

}


@Override
public void addUser(UserReq req, StreamObserver<UserRes> responseObserver)
{
    String userName = req.getName();
    if (users.containsKey(userName)) {
        responseObserver.onNext(UserRes.newBuilder()
                .setIsSuccess(false)
                .setError("User already exists.")
                .build());
    } else {
        users.put(userName, userName);
        responseObserver.onNext(UserRes.newBuilder()
                .setIsSuccess(true)
                .build());
    }
    responseObserver.onCompleted();

    
}

@Override
public void follow(UserReq req, StreamObserver<UserRes> responseObserver)
{
    String userName = req.getName();
    String followUserName = req.getFollowName();
    if (!users.containsKey(userName) || !users.containsKey(followUserName)) {
        responseObserver.onNext(UserRes.newBuilder()
                .setIsSuccess(false)
                .setError("User or follow user does not exist.")
                .build());
    } else {
        following.put(userName, followUserName);
        responseObserver.onNext(UserRes.newBuilder()
                .setIsSuccess(true)
                .build());
    }
    responseObserver.onCompleted();

    
}


@Override
public void viewFollowing(UserReq req, StreamObserver<UserRes> responseObserver) {
    String userName = req.getName();
    if (!users.containsKey(userName)) {
        responseObserver.onNext(UserRes.newBuilder()
                .setIsSuccess(false)
                .setError("User does not exist.")
                .build());
    } else {
        List<String> followedUsers = new ArrayList<>();
        for (Map.Entry<String, String> entry : following.entrySet()) {
            if (entry.getValue().equals(userName)) {
                followedUsers.add(entry.getKey());
            }
        }
   
        if (followedUsers.isEmpty()) {
            responseObserver.onNext(UserRes.newBuilder()
                .setIsSuccess(true)
                .setError("No followed users found.")
                .build());
        } else {
            UserRes.Builder userResBuilder = UserRes.newBuilder()
                    .setIsSuccess(true);
            for (String followedUser : followedUsers) {
                userResBuilder.addFollowedUser(followedUser);
            }
            responseObserver.onNext(userResBuilder.build());
        }
    }
    responseObserver.onCompleted();
}


}