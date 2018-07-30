package com.example.administrator.finalprocject.Codecfactory;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class Lcoderfactory implements ProtocolCodecFactory {
    private LDecoder lDecoder;
    private LEncoder lEncoder;

    public Lcoderfactory()
    {
        lDecoder=new LDecoder();
        lEncoder=new LEncoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return lEncoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return lDecoder;
    }
}
