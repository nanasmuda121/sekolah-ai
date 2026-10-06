import React, { useState, useEffect, useRef } from 'react';
import {
  SafeAreaView,
  View,
  Text,
  FlatList,
  Image,
  StyleSheet,
  StatusBar,
  TouchableOpacity,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { launchCamera, launchImageLibrary } from 'react-native-image-picker';
import TextRecognition from '@react-native-ml-kit/text-recognition';
import { PromptBar } from './src/components/PromptBar';
import { SUBJECTS, SubjectType } from './src/prompts/teacherPrompts';
import { NativeAI } from './src/native/NativeAI';

interface ChatMessage {
  id: string;
  sender: 'user' | 'ai';
  text: string;
  subject?: SubjectType;
  imageUri?: string;
  timestamp: string;
}

export default function App() {
  const [selectedSubject, setSelectedSubject] = useState<SubjectType>('MTK');
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [isGenerating, setIsGenerating] = useState(false);
  const [attachedImage, setAttachedImage] = useState<{ uri: string; text?: string } | null>(null);

  const flatListRef = useRef<FlatList>(null);

  useEffect(() => {
    // Inisialisasi Native C++ AI saat aplikasi pertama kali dibuka
    NativeAI.init().then((success) => {
      console.log('Native AI initialized:', success);
    });
  }, []);

  const handleCameraPress = async () => {
    try {
      const result = await launchCamera({
        mediaType: 'photo',
        quality: 0.8,
        saveToPhotos: false,
      });

      if (result.assets && result.assets[0]?.uri) {
        const uri = result.assets[0].uri;
        let recognizedText = '';

        try {
          // Vision Stage: Ekstrak teks & angka dari foto
          const ocrResult = await TextRecognition.recognize(uri);
          recognizedText = ocrResult.text.trim();
        } catch (ocrErr) {
          console.warn('OCR error or fallback:', ocrErr);
        }

        setAttachedImage({
          uri,
          text: recognizedText,
        });
      }
    } catch (err) {
      console.warn('Camera launch error:', err);
    }
  };

  const handleSendMessage = async (text: string, subject: SubjectType) => {
    const timeNow = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    let fullQuery = text;
    let imgUri = attachedImage?.uri;

    if (attachedImage?.text) {
      fullQuery = `[Teks dari Foto Soal]:\n${attachedImage.text}\n\n[Pertanyaan Siswa]:\n${text || 'Tolong jelaskan dan selesaikan soal pada gambar di atas.'}`;
    }

    const userMsg: ChatMessage = {
      id: Date.now().toString(),
      sender: 'user',
      text: text || (attachedImage ? '📷 Foto Soal' : ''),
      subject,
      imageUri: imgUri,
      timestamp: timeNow,
    };

    setMessages((prev) => [...prev, userMsg]);
    setAttachedImage(null);
    setIsGenerating(true);

    // Scroll to bottom
    setTimeout(() => {
      flatListRef.current?.scrollToEnd({ animated: true });
    }, 100);

    try {
      const subjectConfig = SUBJECTS[subject];
      const aiReply = await NativeAI.ask(fullQuery, subjectConfig.systemPrompt);

      const aiMsg: ChatMessage = {
        id: (Date.now() + 1).toString(),
        sender: 'ai',
        text: aiReply,
        subject,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      };

      setMessages((prev) => [...prev, aiMsg]);
    } catch (e) {
      const errMsg: ChatMessage = {
        id: (Date.now() + 1).toString(),
        sender: 'ai',
        text: 'Maaf, terjadi kendala saat memproses jawaban. Silakan coba kembali.',
        subject,
        timestamp: timeNow,
      };
      setMessages((prev) => [...prev, errMsg]);
    } finally {
      setIsGenerating(false);
      setTimeout(() => {
        flatListRef.current?.scrollToEnd({ animated: true });
      }, 100);
    }
  };

  const renderMessageItem = ({ item }: { item: ChatMessage }) => {
    const isUser = item.sender === 'user';
    const subjectInfo = item.subject ? SUBJECTS[item.subject] : SUBJECTS.MTK;

    return (
      <View
        style={[
          styles.messageRow,
          isUser ? styles.messageRowUser : styles.messageRowAI,
        ]}
      >
        {!isUser && (
          <View style={[styles.avatarAI, { backgroundColor: subjectInfo.color }]}>
            <Text style={styles.avatarIcon}>{subjectInfo.icon}</Text>
          </View>
        )}

        <View
          style={[
            styles.messageBubble,
            isUser ? styles.bubbleUser : styles.bubbleAI,
          ]}
        >
          {item.imageUri && (
            <Image
              source={{ uri: item.imageUri }}
              style={styles.messageImage}
              resizeMode="cover"
            />
          )}

          <Text style={[styles.messageText, isUser ? styles.textUser : styles.textAI]}>
            {item.text}
          </Text>

          <Text style={[styles.timestamp, isUser ? styles.timeUser : styles.timeAI]}>
            {item.timestamp}
          </Text>
        </View>
      </View>
    );
  };

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor="#09090b" />

      {/* Top Header */}
      <View style={styles.header}>
        <View>
          <Text style={styles.headerTitle}>Sekolah AI 🎓</Text>
          <Text style={styles.headerSubtitle}>Tutor Pintar 100% Offline • RAM Safe</Text>
        </View>
        <View style={styles.badgeContainer}>
          <View style={styles.onlineDot} />
          <Text style={styles.badgeText}>Offline AI</Text>
        </View>
      </View>

      {/* Main Chat Area */}
      <KeyboardAvoidingView
        style={styles.chatArea}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >
        {messages.length === 0 ? (
          <View style={styles.emptyContainer}>
            <Text style={styles.emptyIcon}>📚</Text>
            <Text style={styles.emptyTitle}>Tanya Soal Pelajaran Apa Saja</Text>
            <Text style={styles.emptyDesc}>
              Bisa foto soal matematika (Pythagoras, aljabar), tanya sejarah, bahasa Inggris, PKn, atau agama tanpa kuota internet!
            </Text>

            <View style={styles.quickCards}>
              <TouchableOpacity
                style={styles.quickCard}
                onPress={() => {
                  setSelectedSubject('MTK');
                  handleSendMessage('Segitiga siku-siku alas 6 cm dan tinggi 8 cm, berapa sisi miringnya?', 'MTK');
                }}
              >
                <Text style={styles.quickCardText}>📐 Hitung Pythagoras: alas 6 & tinggi 8</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.quickCard}
                onPress={() => {
                  setSelectedSubject('SEJARAH');
                  handleSendMessage('Jelaskan peristiwa Rengasdengklok menjelang kemerdekaan RI', 'SEJARAH');
                }}
              >
                <Text style={styles.quickCardText}>🏛️ Peristiwa Rengasdengklok</Text>
              </TouchableOpacity>
            </View>
          </View>
        ) : (
          <FlatList
            ref={flatListRef}
            data={messages}
            renderItem={renderMessageItem}
            keyExtractor={(item) => item.id}
            contentContainerStyle={styles.messageList}
            onContentSizeChange={() => flatListRef.current?.scrollToEnd({ animated: true })}
          />
        )}

        {/* ReactBits-Inspired PromptBar Component */}
        <PromptBar
          selectedSubject={selectedSubject}
          onSelectSubject={setSelectedSubject}
          onSend={handleSendMessage}
          onCameraPress={handleCameraPress}
          isGenerating={isGenerating}
          onStopGenerating={() => setIsGenerating(false)}
          hasAttachment={!!attachedImage}
          attachmentName={attachedImage?.text ? 'Soal Terdeteksi dari Foto' : 'Foto Soal'}
          onRemoveAttachment={() => setAttachedImage(null)}
        />
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#09090b',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#18181b',
  },
  headerTitle: {
    fontSize: 18,
    fontWeight: '800',
    color: '#f4f4f5',
  },
  headerSubtitle: {
    fontSize: 11,
    color: '#71717a',
    marginTop: 2,
  },
  badgeContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#18181b',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#27272a',
  },
  onlineDot: {
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: '#10b981',
    marginRight: 6,
  },
  badgeText: {
    fontSize: 10,
    fontWeight: '600',
    color: '#a1a1aa',
  },
  chatArea: {
    flex: 1,
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 32,
  },
  emptyIcon: {
    fontSize: 48,
    marginBottom: 12,
  },
  emptyTitle: {
    fontSize: 18,
    fontWeight: '700',
    color: '#e4e4e7',
    textAlign: 'center',
    marginBottom: 8,
  },
  emptyDesc: {
    fontSize: 13,
    color: '#71717a',
    textAlign: 'center',
    lineHeight: 18,
    marginBottom: 24,
  },
  quickCards: {
    width: '100%',
    gap: 8,
  },
  quickCard: {
    backgroundColor: '#18181b',
    borderWidth: 1,
    borderColor: '#27272a',
    paddingVertical: 10,
    paddingHorizontal: 14,
    borderRadius: 12,
  },
  quickCardText: {
    fontSize: 12,
    color: '#d4d4d8',
    fontWeight: '500',
  },
  messageList: {
    paddingHorizontal: 12,
    paddingVertical: 16,
  },
  messageRow: {
    flexDirection: 'row',
    marginBottom: 14,
    maxWidth: '88%',
  },
  messageRowUser: {
    alignSelf: 'flex-end',
    justifyContent: 'flex-end',
  },
  messageRowAI: {
    alignSelf: 'flex-start',
    justifyContent: 'flex-start',
  },
  avatarAI: {
    width: 28,
    height: 28,
    borderRadius: 14,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 8,
    marginTop: 2,
  },
  avatarIcon: {
    fontSize: 14,
  },
  messageBubble: {
    borderRadius: 16,
    paddingHorizontal: 14,
    paddingVertical: 10,
  },
  bubbleUser: {
    backgroundColor: '#2563eb',
    borderBottomRightRadius: 4,
  },
  bubbleAI: {
    backgroundColor: '#18181b',
    borderWidth: 1,
    borderColor: '#27272a',
    borderBottomLeftRadius: 4,
  },
  messageImage: {
    width: 200,
    height: 140,
    borderRadius: 10,
    marginBottom: 8,
  },
  messageText: {
    fontSize: 14,
    lineHeight: 20,
  },
  textUser: {
    color: '#ffffff',
  },
  textAI: {
    color: '#f4f4f5',
  },
  timestamp: {
    fontSize: 10,
    marginTop: 4,
    alignSelf: 'flex-end',
  },
  timeUser: {
    color: '#bfdbfe',
  },
  timeAI: {
    color: '#71717a',
  },
});
