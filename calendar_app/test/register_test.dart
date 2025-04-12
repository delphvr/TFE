import 'package:http/http.dart' as http;
import 'package:calendar_app/auth/register.dart';
import 'package:mockito/annotations.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter/material.dart';
import 'dart:convert';
import 'package:firebase_auth/firebase_auth.dart';

import 'register_test.mocks.dart';

//https://docs.flutter.dev/cookbook/testing/unit/mocking
//https://docs.flutter.dev/cookbook/testing/widget/tap-drag

@GenerateMocks([http.Client, FirebaseAuth, User, UserCredential])
void main() {
  group('register', () {
    late MockClient client;
    late MockFirebaseAuth mockAuth;
    late MockUserCredential mockUserCredential;

    setUp(() async {
      await dotenv.load(fileName: "lib/.env");
      client = MockClient();
      mockAuth = MockFirebaseAuth();
      mockUserCredential = MockUserCredential();

      when(client.get(Uri.parse('${dotenv.env['API_BASE_URL']}/professions')))
          .thenAnswer((_) async => http.Response('''[
            {"profession": "Danseur"},
            {"profession": "Comédien"},
            {"profession": "Musicien"}
          ]''', 200));

      when(client.post(
        Uri.parse('${dotenv.env['API_BASE_URL']}/users'),
        headers: anyNamed('headers'),
        body: anyNamed('body'),
      )).thenAnswer((invocation) async {
        final body = jsonDecode(invocation.namedArguments[#body] as String);
        if (body['email'] == 'eve@mail.com' &&
            body['firstName'] == 'Eve' &&
            body['lastName'] == 'Pley') 
        {
          return http.Response(jsonEncode({'id': 1}), 201);
        } else {
          return http.Response('Invalid Data', 400);
        }
      });

      when(mockAuth.createUserWithEmailAndPassword(
        email: anyNamed('email'),
        password: anyNamed('password'),
      )).thenAnswer((_) async => mockUserCredential);
    });

    testWidgets('register with incorrect email', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: Register(onTap: () {}, client: client, auth: mockAuth,
),
      ));

      await tester.enterText(find.byKey(const Key('firstNameField')), 'Eve');
      await tester.enterText(find.byKey(const Key('lastNameField')), 'Pley');
      await tester.enterText(
          find.byKey(const Key('emailField')), 'evemail.com');
      await tester.enterText(
          find.byKey(const Key('passwordField')), 'password');
      await tester.enterText(
          find.byKey(const Key('confirmPasswordField')), 'password');

      await tester.tap(find.text('Créer mon compte'));
      await tester.pumpAndSettle();
      expect(find.text('Email non valide'), findsOneWidget);
    });

    testWidgets('register with incorrect password (at least 7 char)', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: Register(onTap: () {}, client: client, auth: mockAuth,
),
      ));

      await tester.enterText(find.byKey(const Key('firstNameField')), 'Eve');
      await tester.enterText(find.byKey(const Key('lastNameField')), 'Pley');
      await tester.enterText(
          find.byKey(const Key('emailField')), 'eve@mail.com');
      await tester.enterText(
          find.byKey(const Key('passwordField')), '12345');
      await tester.enterText(
          find.byKey(const Key('confirmPasswordField')), '12345');

      await tester.tap(find.text('Créer mon compte'));
      await tester.pumpAndSettle();
      //debugDumpApp();
      expect(find.text('Le mot de passe doit faire au moins 6 caractères.'), findsOneWidget);
    });
  });
}
