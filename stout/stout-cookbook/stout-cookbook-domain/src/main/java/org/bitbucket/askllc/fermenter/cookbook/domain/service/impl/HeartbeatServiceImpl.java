package org.bitbucket.askllc.fermenter.cookbook.domain.service.impl;

import org.bitbucket.askllc.fermenter.cookbook.domain.service.rest.HeartbeatService;
import org.bitbucket.fermenter.stout.messages.Message;
import org.bitbucket.fermenter.stout.messages.MessageManager;
import org.bitbucket.fermenter.stout.messages.MetaMessage;
import org.bitbucket.fermenter.stout.messages.Severity;
import org.springframework.stereotype.Service;

/**
 * Service implementation class for the Heartbeat service.
 * 
 * @see org.bitbucket.askllc.fermenter.cookbook.domain.service.rest.HeartbeatService
 *
 *      GENERATED STUB CODE - PLEASE *DO* MODIFY
 */
@Service
public class HeartbeatServiceImpl extends HeartbeatBaseServiceImpl implements HeartbeatService {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void heartbeatImpl() {
        Message message = new Message(new HeartbeatMessage(), Severity.INFO);

        // nothing to do here, this is just to check that the server and service responds
        // primarily implemented to support testing a get/supports call with a void response
        MessageManager.addMessage(message);
    }

    private class HeartbeatMessage implements MetaMessage {

        @Override
        public String toString() {
            return "heartbeat";
        }

        @Override
        public String getText() {
            return "lub dub";
        }

    }

}