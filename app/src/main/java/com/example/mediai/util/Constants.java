package com.example.mediai.util;

public final class Constants {
    public static final String APP_NAME = "Medi AI";

    // AI Provider - Default to DEMO for academic project
    public static final String AI_PROVIDER = "DEMO";
    // Available providers: "DEMO", "MISTRAL", "DEEPSEEK"

    // Mistral API
    public static final String MISTRAL_BASE_URL = "https://api.mistral.ai/";
    public static final String MISTRAL_CHAT_PATH = "v1/chat/completions";
    public static final String MISTRAL_MODEL = "mistral-large-latest";

    // DeepSeek API
    public static final String DEEPSEEK_BASE_URL = "https://api.deepseek.com/";
    public static final String DEEPSEEK_CHAT_PATH = "chat/completions";
    public static final String DEEPSEEK_MODEL = "deepseek-v4-flash";

    // SharedPreferences keys
    public static final String PREF_NAME = "medi_ai_prefs";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_API_KEY = "ai_api_key";
    public static final String KEY_AI_PROVIDER = "ai_provider";

    // AI System Prompt
    public static final String SYSTEM_PROMPT = "Eres Medi AI, un asistente de apoyo para una app de organización de medicamentos. " +
            "Responde en español, con lenguaje claro, breve y responsable. " +
            "Puedes ayudar a organizar recordatorios, explicar de forma general qué significan indicaciones comunes y sugerir " +
            "que el usuario revise la etiqueta o consulte a un profesional. " +
            "No diagnostiques enfermedades, no cambies dosis, no indiques suspender medicamentos y no reemplaces a un médico. " +
            "Si el usuario menciona síntomas graves, reacción alérgica, dificultad para respirar, dolor intenso, sobredosis " +
            "o emergencia, recomienda buscar atención médica urgente.";

    // Notification
    public static final String NOTIFICATION_CHANNEL_ID = "medi_ai_reminders";
    public static final int NOTIFICATION_ID_BASE = 1000;
    public static final String EXTRA_MEDICATION_ID = "medication_id";
    public static final String EXTRA_MEDICATION_NAME = "medication_name";
    public static final String EXTRA_MEDICATION_DOSE = "medication_dose";

    // Export
    public static final String PDF_DIRECTORY = "MediAI_Reports";
    public static final String PDF_FILE_PREFIX = "Reporte_MediAI_";

    private Constants() {
        // Private constructor to prevent instantiation
    }
}