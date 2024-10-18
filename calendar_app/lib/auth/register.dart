import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
//source: https://www.youtube.com/watch?v=qlVj-0vpaW0

class Register extends StatefulWidget {
  final Function()? onTap;
  const Register({super.key, required this.onTap});

  @override
  State<Register> createState() => _RegisterState();
}

class _RegisterState extends State<Register> {
  final emailController = TextEditingController();
  final passwordController = TextEditingController();
  final confirmpasswordController = TextEditingController();

  void login() async{
    showDialog(context: context, builder: (context){
      return const Center(
        child: CircularProgressIndicator(),
      );
    },);

    try {
      if(emailController.text.isEmpty || passwordController.text.isEmpty || confirmpasswordController.text.isEmpty){
        if (mounted) {
          Navigator.pop(context);
        }
        errorMess('Merci de remplir tous les champs');
      }else{
        if(passwordController.text == confirmpasswordController.text){
          await FirebaseAuth.instance.createUserWithEmailAndPassword(
            email: emailController.text, 
            password: passwordController.text);
          if (mounted) {
            Navigator.pop(context);
          }
        } else {
          if (mounted) {
            Navigator.pop(context);
          }
          errorMess('Les mots de passe ne correspondent pas');
        }
      }
    } on FirebaseAuthException catch(e){
      if (mounted) {
          Navigator.pop(context);
      }
      if(e.code == "weak-password"){
        errorMess("Le mot de passe doit faire au moins 6 caractères.");
      }if(e.code == "invalid-email"){
        errorMess("Email non valide");
      }else{
        errorMess(e.code);
      }
    }
  }

  void errorMess(String message){
    showDialog(
      context: context, 
      builder: (context){
        return AlertDialog(
          title: const Text('Erreur lors de la création du compte'),
          content: Text(message),
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
                  'Créer un Compte',
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
            
                const SizedBox(height:25),
            
                TextFieldcustom(labelText: 'Confirmer le mot de passe', controller: confirmpasswordController, obscureText: true,),
            
                const SizedBox(height:25),
            
                //button se connecter
                ButtonCustom(
                  text: 'Créer mon compte',
                  onTap: login,
                  ),
            
                const SizedBox(height:25),
            
                //creer un compte
                GestureDetector(
                  onTap: widget.onTap,
                  child: const Text('A déjà un compte, se connecter',
                                    style: TextStyle(
                                    fontSize: 17.0,)
                    )
                ),
            
              ],
            ),
          ),
        ),
        )
    );
  }
}