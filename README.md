# java-explore-with-me
Template repository for graduation project.

### Stage 1. Statistics Service
* Collects information about the number of user requests to event lists
* Collects information about the number of requests for detailed event information
* Statistics on the operation of the application are generated based on this information.

### Stage 2. Main Service
* The work is being carried out with:
* - events
* - by categories
* - by users
* - selection of events
* - requests for participation

### Stage 3. Additional functionality
* Functionality for working with event comments
* Functional tests for Postman

#### Admin: Comments:
> PATCH: /admin/comments/{commentId}
>
> Editing and publishing a comment with the identifier `commentId`, at the output we get `DTO.CommentResponse`, send it to the entrance `DTO.CommentAdminRequest`.
>
> DELETE: /admin/comments/{commentId}
>
> Deleting a user comment with the identifier `commentId`, get nothing at the output, no additional parameters at the input.
> 
> GET: /admin/comments/{commentId}
>
> Getting a user comment with the identifier `commentId`, at the output we get `DTO.CommentResponse`, no additional parameters at the input.
> 
> GET: /admin/comments
>
> Getting all user unpublished comments, at the output we get collection from `DTO.CommentResponse`, no additional parameters at the input.

#### Private: Comments:
> POST: /users/{userId}/events/{eventId}/comments
>
> Creating a user comment with the identifier `userId` by event with identifier `eventId`, at the output we get `DTO.CommentResponse`, send it to the entrance `DTO.CommentUserRequest`
> 
> PATCH: /users/{userId}/events/{eventId}/comments/{commentId}
>
> Updating a user comment with the identifier `userId` by event `eventId`, at the output we get `DTO.CommentResponse`, send it to the entrance `DTO.CommentUserRequest` and comment identifier `commentId`.
> 
> GET: /users/{userId}/events/{eventId}/comments/{commentId}
>
> Getting a user comment with the identifier `userId` by event `eventId`, at the output we get `DTO.CommentResponse`, send it to the entrance comment identifier `commentId`.
> 
> GET: /users/{userId}/events/{eventId}/comments
>
> Getting all user comments with the identifier `userId` by event `eventId`, at the output we get `DTO.EventCommentFullListResponseDto`

#### Public: Comments:
> GET: events/{eventId}/comments
>
> Getting all user comments published by the administrator (based on accepted) by event `eventId`, at the output we get `DTO.EventShortCommentsResponseDto`