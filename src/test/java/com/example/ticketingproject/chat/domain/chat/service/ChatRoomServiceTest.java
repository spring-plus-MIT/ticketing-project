package com.example.ticketingproject.chat.domain.chat.service;

import com.example.ticketingproject.chat.domain.chat.dto.ChatRoomResponse;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoom;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoomStatus;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    private User normalUser;
    private User adminUser;
    private ChatRoom chatRoom;

    @BeforeEach
    void setUp() {
        normalUser = User.builder().build();
        ReflectionTestUtils.setField(normalUser, "id", 1L);
        ReflectionTestUtils.setField(normalUser, "userRole", UserRole.USER);
        ReflectionTestUtils.setField(normalUser, "name", "일반유저");

        adminUser = User.builder().build();
        ReflectionTestUtils.setField(adminUser, "id", 2L);
        ReflectionTestUtils.setField(adminUser, "userRole", UserRole.ADMIN);
        ReflectionTestUtils.setField(adminUser, "name", "관리자");

        chatRoom = ChatRoom.builder()
                .status(ChatRoomStatus.WAITING)
                .creator(normalUser)
                .build();
        ReflectionTestUtils.setField(chatRoom, "id", 1L);
    }

    @Test
    @DisplayName("채팅방 생성 성공 - 무조건 WAITING 상태로 생성된다")
    void createChatRoom_success() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(normalUser));
        given(chatRoomRepository.save(any(ChatRoom.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ChatRoomResponse response = chatRoomService.createChatRoom(1L);

        // then
        assertThat(response.getStatus()).isEqualTo(ChatRoomStatus.WAITING);
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방 목록 조회 - 관리자는 전체 방을 조회한다")
    void getChatRooms_admin() {
        // given
        given(userRepository.findById(2L)).willReturn(Optional.of(adminUser));
        given(chatRoomRepository.findAll()).willReturn(List.of(chatRoom));

        // when
        List<ChatRoomResponse> responses = chatRoomService.getChatRooms(2L);

        // then
        assertThat(responses).hasSize(1);
        verify(chatRoomRepository).findAll();
    }

    @Test
    @DisplayName("채팅방 목록 조회 - 일반 유저는 본인이 만든 방만 조회한다")
    void getChatRooms_user() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(normalUser));
        given(chatRoomRepository.findAllByCreator(normalUser)).willReturn(List.of(chatRoom));

        // when
        List<ChatRoomResponse> responses = chatRoomService.getChatRooms(1L);

        // then
        assertThat(responses).hasSize(1);
        verify(chatRoomRepository).findAllByCreator(normalUser);
    }

    @Test
    @DisplayName("문의 상태 변경 성공")
    void updateRoomStatus_success() {
        // given
        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));

        // when
        chatRoomService.updateRoomStatus(1L, ChatRoomStatus.IN_PROGRESS);

        // then
        assertThat(chatRoom.getStatus()).isEqualTo(ChatRoomStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("존재하지 않는 방의 상태를 변경하려 하면 예외가 발생한다")
    void updateRoomStatus_fail_notFound() {
        // given
        given(chatRoomRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> chatRoomService.updateRoomStatus(999L, ChatRoomStatus.IN_PROGRESS))
                .isInstanceOf(BaseException.class);
    }
}