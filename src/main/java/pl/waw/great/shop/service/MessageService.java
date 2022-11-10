package pl.waw.great.shop.service;

import org.springframework.stereotype.Service;
import pl.waw.great.shop.model.Message;
import pl.waw.great.shop.model.dto.MessageDto;
import pl.waw.great.shop.model.mapper.MessageMapper;
import pl.waw.great.shop.repository.MessageRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public MessageService(MessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    public MessageDto create(MessageDto messageDto) {
        Message message = this.messageMapper.messageDtoToMessage(messageDto);
        Message messageCreated = this.messageRepository.create(message);
        return this.messageMapper.messageToMessageDto(messageCreated);
    }
}
