package pl.waw.great.shop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import pl.waw.great.shop.model.Message;
import pl.waw.great.shop.model.dto.MessageDto;
import pl.waw.great.shop.model.mapper.MessageMapper;
import pl.waw.great.shop.repository.MessageRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @Spy
    private MessageMapper messageMapper = Mappers.getMapper(MessageMapper.class);

    private static final String TITLE = "title";
    private static final String TEXT = "txt";
    private static final String CITY = "city";
    private static final String EMAIL = "email";
    private Message message;
    private MessageDto messageDto;

    @BeforeEach
    void setUp() {
        this.message = Message.builder()
                .body(TEXT)
                .title(TITLE)
                .city(CITY)
                .email(EMAIL)
                .build();
        this.messageDto = new MessageDto(TITLE, TEXT, CITY, EMAIL);
    }

    @AfterEach
    void clear() {
        this.messageRepository.deleteAll();
    }

    @Test
    void create() {
        when(this.messageRepository.create(any(Message.class))).thenReturn(this.message);

        MessageDto messageDto = this.messageService.create(this.messageDto);

        assertNotNull(messageDto);
        assertEquals(TITLE, messageDto.getTitle());
        assertEquals(TEXT, messageDto.getBody());
        assertEquals(CITY, messageDto.getCity());
        assertEquals(EMAIL, messageDto.getEmail());
    }
}