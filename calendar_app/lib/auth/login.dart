import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
//source: https://www.youtube.com/watch?v=Dh-cTQJgM-Q

class LoginScreen extends StatefulWidget {
  final Function()? onTap;
  LoginScreen({super.key, required this.onTap});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final emailController = TextEditingController();

  final passwordController = TextEditingController();

  void login() async{
    showDialog(context: context, builder: (context){
      return const Center(
        child: CircularProgressIndicator(),
      );
    },);

    try {
      await FirebaseAuth.instance.signInWithEmailAndPassword(
      email: emailController.text, 
      password: passwordController.text);
      Navigator.pop(context);
    } on FirebaseAuthException catch(e){
      Navigator.pop(context);
      if (e.code == 'invalid-email' || e.code == 'invalid-credential'){
          invalidVredentialErrorMess();
      }else{
        print('FirebaseAuth error: ${e.code}');}
    }
  }

  void invalidVredentialErrorMess(){
    showDialog(
      context: context, 
      builder: (context){
        return AlertDialog(
          title: Text('Erreur de connexion'),
          content: Text('Email ou mot de passe incorect'),
          actions: [
            TextButton(onPressed: () {
              Navigator.pop(context);
              }, 
              child: const Text('OK'),
            ),
          ]
          );
    },);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
            
                //const SizedBox(height:50),
                
                //S identifier
                const Text(
                  'S\'identifier',
                  style: TextStyle(
                    fontSize: 30,
                  ),
                  ),
            
                  const SizedBox(height:50),
            
                //email
                TextFieldcustom(labelText: 'Email', controller: emailController, obscureText: false,),
            
                const SizedBox(height:25),
              
                //mot de passe
                TextFieldcustom(labelText: 'Mot de passe', controller: passwordController, obscureText: true,),
            
                const SizedBox(height:5),
            
                //mot de passe oublie
                //const Text(
                //  'Mot de passe oublié ?'
                //),
            
                const SizedBox(height:25),
            
                //button se connecter
                ButtonCustom(
                  text: 'Se connecter',
                  onTap: login,
                  ),
            
                const SizedBox(height:25),
            
                //creer un compte
                GestureDetector(
                  onTap: widget.onTap,
                  child: const Text('Créer un compte')
                ),
            
              ],
            ),
          ),
        ),
        )
    );
  }
}