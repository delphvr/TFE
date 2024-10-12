import 'package:flutter/material.dart';

class ButtonCustom extends StatelessWidget {
  final String text;
  final Function()? onTap;
  const ButtonCustom({super.key, required this.text, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: 250,
        height: 50,
        decoration: BoxDecoration(
          color: Colors.black,
          borderRadius: BorderRadius.circular(6)
        ),
          child: Center(
            child: Text(text,
            style: const TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: 16,
              )),
          ),
      )
    );
  }
}