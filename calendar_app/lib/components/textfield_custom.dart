import 'package:flutter/material.dart';


class TextFieldcustom extends StatelessWidget {
  final String labelText;
  final controller;
  final bool obscureText;
  const TextFieldcustom({super.key, required this.labelText, required this.controller, required this.obscureText});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
              width: 250,
              child: TextField(
                controller: controller,
                obscureText: obscureText,
                decoration: InputDecoration(
                  border: const OutlineInputBorder(),
                  labelText: labelText,
                  fillColor: const Color(0xFFF2F2F2),
                  filled: true,
                ),
              ),
            );
  }
}