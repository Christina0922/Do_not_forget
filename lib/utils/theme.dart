import 'package:flutter/material.dart';

class AppTheme {
  // 색상 정의
  static const darkNavy = Color(0xFF0A0E27);
  static const charcoal = Color(0xFF1A1F3A);
  static const cardBackground = Color(0xFF252B48);
  static const limeAccent = Color(0xFF39FF14);
  static const limeDim = Color(0xFF2DD10D);
  static const textPrimary = Color(0xFFE8E8E8);
  static const textSecondary = Color(0xFFB0B0B0);
  static const errorRed = Color(0xFFFF4444);
  static const successGreen = Color(0xFF4CAF50);

  static ThemeData get darkTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.dark,
      scaffoldBackgroundColor: darkNavy,
      
      colorScheme: const ColorScheme.dark(
        primary: limeAccent,
        secondary: limeDim,
        surface: cardBackground,
        background: darkNavy,
        error: errorRed,
        onPrimary: darkNavy,
        onSecondary: darkNavy,
        onSurface: textPrimary,
        onBackground: textPrimary,
      ),
      
      cardTheme: CardTheme(
        color: cardBackground,
        elevation: 2,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(14),
        ),
      ),
      
      appBarTheme: const AppBarTheme(
        backgroundColor: darkNavy,
        elevation: 0,
        centerTitle: false,
        titleTextStyle: TextStyle(
          color: textPrimary,
          fontSize: 24,
          fontWeight: FontWeight.bold,
        ),
      ),
      
      textTheme: const TextTheme(
        headlineMedium: TextStyle(
          fontSize: 24,
          fontWeight: FontWeight.bold,
          color: textPrimary,
        ),
        titleLarge: TextStyle(
          fontSize: 20,
          fontWeight: FontWeight.bold,
          color: textPrimary,
        ),
        titleMedium: TextStyle(
          fontSize: 18,
          fontWeight: FontWeight.w600,
          color: textPrimary,
        ),
        bodyLarge: TextStyle(
          fontSize: 16,
          color: textPrimary,
        ),
        bodyMedium: TextStyle(
          fontSize: 14,
          color: textSecondary,
        ),
      ),
      
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: limeAccent,
          foregroundColor: darkNavy,
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10),
          ),
          textStyle: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
      
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: limeAccent,
          side: const BorderSide(color: limeAccent, width: 1.5),
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10),
          ),
        ),
      ),
      
      switchTheme: SwitchThemeData(
        thumbColor: MaterialStateProperty.resolveWith((states) {
          if (states.contains(MaterialState.selected)) {
            return limeAccent;
          }
          return textSecondary;
        }),
        trackColor: MaterialStateProperty.resolveWith((states) {
          if (states.contains(MaterialState.selected)) {
            return limeAccent.withOpacity(0.5);
          }
          return textSecondary.withOpacity(0.3);
        }),
      ),
    );
  }
}

