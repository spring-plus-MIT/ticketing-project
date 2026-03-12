package com.example.ticketingproject.chat.domain.chat.service;

import com.example.ticketingproject.chat.domain.chat.dto.ChatMessageRequest;
import com.example.ticketingproject.chat.domain.chat.dto.ChatMessageResponse;
import com.example.ticketingproject.chat.domain.chat.entity.ChatMessage;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoom;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoomStatus;
import com.example.ticketingproject.chat.domain.chat.repository.ChatMessageRepository;
import com.example.ticketingproject.chat.domain.chat.repository.ChatRoomRepository;
import com.example.ticketingproject.common.exception.BaseException;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    private User sender;
    private User admin;
    private User stranger;
    private ChatRoom chatRoom;
    private ChatMessage chatMessage;
    private ChatMessageRequest request;

    @BeforeEach
    void setUp() {
        sender = User.builder().build();
        ReflectionTestUtils.setField(sender, "id", 1L);
        ReflectionTestUtils.setField(sender, "name", "테스터");
        ReflectionTestUtils.setField(sender, "userRole", UserRole.USER);

        admin = User.builder().build();
        ReflectionTestUtils.setField(admin, "id", 2L);
        ReflectionTestUtils.setField(admin, "name", "관리자");
        ReflectionTestUtils.setField(admin, "userRole", UserRole.ADMIN);

        stranger = User.builder().build();
        ReflectionTestUtils.setField(stranger, "id", 3L);
        ReflectionTestUtils.setField(stranger, "name", "침입자");
        ReflectionTestUtils.setField(stranger, "userRole", UserRole.USER);

        chatRoom = ChatRoom.builder()
                .status(ChatRoomStatus.WAITING)
                .creator(sender)
                .build();
        ReflectionTestUtils.setField(chatRoom, "id", 1L);

        request = new ChatMessageRequest();
        ReflectionTestUtils.setField(request, "roomId", 1L);
        ReflectionTestUtils.setField(request, "content", "안녕하세요!");

        chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(request.getContent())
                .build();
        ReflectionTestUtils.setField(chatMessage, "id", 100L);
    }

    @Test
    @DisplayName("메시지 저장 성공 - 본인이 만든 방에 메시지를 보낸다")
    void saveMessage_success_creator() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(sender));
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
        given(chatMessageRepository.save(any(ChatMessage.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ChatMessageResponse response = chatService.saveMessage(request, 1L);

        // then
        assertThat(response.getContent()).isEqualTo("안녕하세요!");
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("메시지 저장 성공 - 관리자는 남의 방이라도 메시지를 보낼 수 있다")
    void saveMessage_success_admin() {
        // given
        given(userRepository.findById(2L)).willReturn(Optional.of(admin));
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
        given(chatMessageRepository.save(any(ChatMessage.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ChatMessageResponse response = chatService.saveMessage(request, 2L);

        // then
        assertThat(response.getContent()).isEqualTo("안녕하세요!");
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("메시지 저장 실패 - 본인이 만든 방도 아니고, 관리자도 아니면 예외가 발생한다 (권한 없음)")
    void saveMessage_fail_forbidden() {
        // given
        given(userRepository.findById(3L)).willReturn(Optional.of(stranger));
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));

        // when & then
        assertThatThrownBy(() -> chatService.saveMessage(request, 3L))
                .isInstanceOf(BaseException.class);
    }

    @Test
    @DisplayName("과거 메시지 조회 - 처음 입장 시 최신 메시지를 가져온다 (lastMessageId == null)")
    void getMessageHistory_initial() {
        // given
        given(chatMessageRepository.findLatestMessages(eq(1L), any(Pageable.class)))
                .willReturn(List.of(chatMessage));

        // when
        List<ChatMessageResponse> history = chatService.getMessageHistory(1L, null, 30);

        // then
        assertThat(history).hasSize(1);
        verify(chatMessageRepository).findLatestMessages(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("과거 메시지 조회 - 스크롤 시 이전 메시지를 가져온다 (lastMessageId != null)")
    void getMessageHistory_scrolling() {
        // given
        Long lastMessageId = 100L;
        given(chatMessageRepository.findMessagesBefore(eq(1L), eq(lastMessageId), any(Pageable.class)))
                .willReturn(List.of(chatMessage));

        // when
        List<ChatMessageResponse> history = chatService.getMessageHistory(1L, lastMessageId, 30);

        // then
        assertThat(history).hasSize(1);
        verify(chatMessageRepository).findMessagesBefore(eq(1L), eq(lastMessageId), any(Pageable.class));
    }
}