package com.backend;

import com.iflytek.cloud.speech.LexiconListener;
import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechUtility;
import com.iflytek.cloud.speech.UserWords;
import org.apache.http.entity.ContentType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.backend.util.StaticData.APPID;

public class VoiceRecognition {
    private static StringBuilder curRet;
    private final SpeechRecognizer recognizer;
    private final Object lock = new Object();
    private final RecognizerListener recListener = new RecognizerListener() {
        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onResult(RecognizerResult results, boolean islast) {
            String text = results.getResultString();
            curRet.append(text);
            if (islast) {
                synchronized (lock) {
                    lock.notify();
                }
            }
        }

        @Override
        public void onVolumeChanged(int volume) {
        }

        @Override
        public void onError(SpeechError error) {
        }

        @Override
        public void onEvent(int eventType, int arg1, int agr2, String msg) {
        }
    };
    LexiconListener lexiconListener = (lexiconId, error) -> {
    };

    public VoiceRecognition() {
        this.recognizer = SpeechRecognizer.createRecognizer();
        SpeechUtility.createUtility(SpeechConstant.APPID + "=" + APPID);
        this.uploadUserWords();
    }

    public static String recognize(String path) throws IOException {
        VoiceRecognition iatTool = new VoiceRecognition();
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
        return iatTool.RecognizeWAVByte(multipartFile);
    }

    private void uploadUserWords() {
        UserWords userwords = new UserWords();
        userwords.putWord("去");
        userwords.putWord("桌子");
        userwords.putWord("餐桌");
        userwords.putWord("客厅");
        userwords.putWord("卧室");
        userwords.putWord("拿");
        userwords.putWord("东西");
        userwords.putWord("拿东西");
        userwords.putWord("放到");
        userwords.putWord("拿东西放到");
        userwords.putWord("拿东西放到客厅");
        userwords.putWord("目的地");


        recognizer.setParameter(SpeechConstant.DATA_TYPE, "userword");
        recognizer.updateLexicon("userwords", userwords.toString(), lexiconListener);
    }

    public String RecognizeWAVByte(MultipartFile audioFile) {

        curRet = new StringBuilder();

        try {
            recognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
            recognizer.setParameter(SpeechConstant.RESULT_TYPE, "plain");
            recognizer.setParameter(SpeechConstant.VAD_BOS, "5000");//前端点超时，
            recognizer.setParameter(SpeechConstant.VAD_EOS, "10000");//后端点超时要与运行SDK时配置的一样

            recognizer.startListening(recListener);

            byte[] buffer = audioFile.getBytes();
            if (buffer.length == 0) {
//                LOGGER.error("no audio avaible!");
                recognizer.cancel();
            } else {

                int lenRead = buffer.length;
                System.out.println("WAV length: " + buffer.length);
                recognizer.writeAudio(buffer, 0, lenRead);
                recognizer.stopListening();
                synchronized (lock) {
                    lock.wait();//主线程等待
                }
                System.out.println("Recognition: " + curRet.toString());
                return curRet.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}