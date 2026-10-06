import React, { useState } from 'react';
import {
  View,
  TextInput,
  TouchableOpacity,
  Text,
  StyleSheet,
  ActivityIndicator,
  ScrollView,
  Platform,
} from 'react-native';
import { SUBJECTS, SubjectType } from '../prompts/teacherPrompts';

interface PromptBarProps {
  onSend: (text: string, subject: SubjectType) => void;
  onCameraPress: () => void;
  onGalleryPress?: () => void;
  isGenerating?: boolean;
  onStopGenerating?: () => void;
  selectedSubject: SubjectType;
  onSelectSubject: (subject: SubjectType) => void;
  hasAttachment?: boolean;
  attachmentName?: string;
  onRemoveAttachment?: () => void;
}

export const PromptBar: React.FC<PromptBarProps> = ({
  onSend,
  onCameraPress,
  isGenerating = false,
  onStopGenerating,
  selectedSubject,
  onSelectSubject,
  hasAttachment = false,
  attachmentName,
  onRemoveAttachment,
}) => {
  const [promptText, setPromptText] = useState('');
  const [isFocused, setIsFocused] = useState(false);

  const canSend = promptText.trim().length > 0 || hasAttachment;

  const handleSend = () => {
    if (isGenerating && onStopGenerating) {
      onStopGenerating();
      return;
    }
    if (!canSend) return;

    onSend(promptText.trim(), selectedSubject);
    setPromptText('');
  };

  return (
    <View style={styles.outerContainer}>
      {/* Subject Chips / Quick Selectors */}
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.subjectScroll}
      >
        {(Object.keys(SUBJECTS) as SubjectType[]).map((subjKey) => {
          const item = SUBJECTS[subjKey];
          const isSelected = selectedSubject === subjKey;
          return (
            <TouchableOpacity
              key={subjKey}
              activeOpacity={0.7}
              style={[
                styles.subjectChip,
                isSelected && { backgroundColor: item.color, borderColor: item.color },
              ]}
              onPress={() => onSelectSubject(subjKey)}
            >
              <Text style={styles.subjectIcon}>{item.icon}</Text>
              <Text
                style={[
                  styles.subjectText,
                  isSelected && styles.subjectTextActive,
                ]}
              >
                {item.label}
              </Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>

      {/* Main Prompt Bar Container (Inspired by ReactBits micro/prompt-bar) */}
      <View
        style={[
          styles.promptBarBox,
          isFocused && styles.promptBarFocused,
        ]}
      >
        {/* Attachment Pill if Photo is Captured */}
        {hasAttachment && (
          <View style={styles.attachmentPill}>
            <Text style={styles.attachmentText}>
              📷 {attachmentName || 'Foto Soal Terlampir'}
            </Text>
            {onRemoveAttachment && (
              <TouchableOpacity
                onPress={onRemoveAttachment}
                hitSlop={{ top: 8, bottom: 8, left: 8, right: 8 }}
                style={styles.removeAttachBtn}
              >
                <Text style={styles.removeAttachText}>✕</Text>
              </TouchableOpacity>
            )}
          </View>
        )}

        <View style={styles.inputRow}>
          {/* Action: Camera Button for Soal / Diagram */}
          <TouchableOpacity
            style={styles.iconButton}
            onPress={onCameraPress}
            activeOpacity={0.7}
            hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
          >
            <View style={styles.cameraIconWrap}>
              <Text style={styles.cameraEmoji}>📷</Text>
            </View>
          </TouchableOpacity>

          {/* Text Input (Auto-expanding, placeholder="Tanya Sesuatu") */}
          <TextInput
            style={styles.input}
            placeholder="Tanya Sesuatu..."
            placeholderTextColor="#71717a"
            value={promptText}
            onChangeText={setPromptText}
            onFocus={() => setIsFocused(true)}
            onBlur={() => setIsFocused(false)}
            multiline
            maxLength={1000}
            textAlignVertical="center"
          />

          {/* Dynamic Send / Stop Button */}
          <TouchableOpacity
            style={[
              styles.sendButton,
              canSend && !isGenerating && styles.sendButtonActive,
              isGenerating && styles.sendButtonStop,
            ]}
            onPress={handleSend}
            disabled={!canSend && !isGenerating}
            activeOpacity={0.8}
          >
            {isGenerating ? (
              <View style={styles.stopSquare} />
            ) : (
              <Text
                style={[
                  styles.sendArrow,
                  canSend ? styles.sendArrowActive : styles.sendArrowDisabled,
                ]}
              >
                ↑
              </Text>
            )}
          </TouchableOpacity>
        </View>

        {/* Footer info: 100% Offline Badge */}
        <View style={styles.bottomMeta}>
          <View style={styles.offlineBadge}>
            <View style={styles.offlineDot} />
            <Text style={styles.offlineText}>100% Offline • RAM Safe (~650MB)</Text>
          </View>
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  outerContainer: {
    paddingHorizontal: 12,
    paddingBottom: Platform.OS === 'ios' ? 24 : 12,
    paddingTop: 4,
    backgroundColor: 'transparent',
  },
  subjectScroll: {
    paddingVertical: 6,
    gap: 6,
  },
  subjectChip: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 6,
    paddingHorizontal: 12,
    borderRadius: 999,
    backgroundColor: '#18181b',
    borderWidth: 1,
    borderColor: '#27272a',
    marginRight: 6,
  },
  subjectIcon: {
    fontSize: 13,
    marginRight: 5,
  },
  subjectText: {
    fontSize: 12,
    fontWeight: '600',
    color: '#a1a1aa',
  },
  subjectTextActive: {
    color: '#ffffff',
  },
  promptBarBox: {
    backgroundColor: '#18181b',
    borderRadius: 20,
    borderWidth: 1,
    borderColor: '#27272a',
    paddingHorizontal: 12,
    paddingVertical: 8,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 8,
    elevation: 4,
  },
  promptBarFocused: {
    borderColor: '#3b82f6',
    shadowColor: '#3b82f6',
    shadowOpacity: 0.25,
  },
  attachmentPill: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#27272a',
    paddingVertical: 5,
    paddingHorizontal: 10,
    borderRadius: 8,
    marginBottom: 6,
  },
  attachmentText: {
    fontSize: 12,
    color: '#e4e4e7',
    flex: 1,
  },
  removeAttachBtn: {
    paddingHorizontal: 6,
  },
  removeAttachText: {
    color: '#a1a1aa',
    fontSize: 13,
    fontWeight: 'bold',
  },
  inputRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  iconButton: {
    padding: 6,
    justifyContent: 'center',
    alignItems: 'center',
  },
  cameraIconWrap: {
    width: 34,
    height: 34,
    borderRadius: 17,
    backgroundColor: '#27272a',
    justifyContent: 'center',
    alignItems: 'center',
  },
  cameraEmoji: {
    fontSize: 16,
  },
  input: {
    flex: 1,
    color: '#f4f4f5',
    fontSize: 14,
    minHeight: 38,
    maxHeight: 100,
    paddingHorizontal: 10,
    paddingVertical: 6,
  },
  sendButton: {
    width: 34,
    height: 34,
    borderRadius: 17,
    backgroundColor: '#27272a',
    justifyContent: 'center',
    alignItems: 'center',
    marginLeft: 6,
  },
  sendButtonActive: {
    backgroundColor: '#3b82f6',
  },
  sendButtonStop: {
    backgroundColor: '#ef4444',
  },
  sendArrow: {
    fontSize: 18,
    fontWeight: '900',
    marginTop: -2,
  },
  sendArrowDisabled: {
    color: '#71717a',
  },
  sendArrowActive: {
    color: '#ffffff',
  },
  stopSquare: {
    width: 12,
    height: 12,
    backgroundColor: '#ffffff',
    borderRadius: 2,
  },
  bottomMeta: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 4,
    paddingTop: 4,
    borderTopWidth: 1,
    borderTopColor: '#27272a',
  },
  offlineBadge: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  offlineDot: {
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: '#10b981',
    marginRight: 6,
  },
  offlineText: {
    fontSize: 10,
    color: '#71717a',
  },
});
