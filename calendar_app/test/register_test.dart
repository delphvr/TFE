import 'package:calendar_app/project/project_user.dart';
import 'package:http/http.dart' as http;
import 'package:calendar_app/auth/register.dart';
import 'package:mockito/annotations.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter/material.dart';
import 'dart:convert';

import 'register_test.mocks.dart';

//https://docs.flutter.dev/cookbook/testing/unit/mocking
//https://docs.flutter.dev/cookbook/testing/widget/tap-drag



@GenerateMocks([http.Client])
void main() {
  group('register', () {
    late MockClient client;

    setUp(() async {
      await dotenv.load(fileName: "lib/.env");
      client = MockClient();
      
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
        print("hey");
        final body = jsonDecode(invocation.namedArguments[#body] as String);
        
        if (body['email'] == 'eve@mail.com' &&
            body['firstName'] == 'Eve' &&
            body['lastName'] == 'Pley' )//&&
            //body['professions'] is List &&
            //(body['professions'] as List).contains('Danseur')) 
            {
              print("here");
          return http.Response(jsonEncode({'id': 1}), 201);
        } else {
          return http.Response('Invalid Data', 400);
        }
      });


    });

    testWidgets('register successfully', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        
        home: Register(onTap: () {}, client: client),
      ));

      await tester.enterText(find.byKey(const Key('firstNameField')), 'Eve');
      await tester.enterText(find.byKey(const Key('lastNameField')), 'Pley');
      await tester.enterText(find.byKey(const Key('emailField')), 'eve@mail.com');
      await tester.enterText(find.byKey(const Key('passwordField')), 'password');
      await tester.enterText(find.byKey(const Key('confirmPasswordField')), 'password');

      //await tester.tap(find.text('Professions'));
      //await tester.pumpAndSettle();
      //await tester.tap(find.text('Danseur'));
      //await tester.pump();
      //await tester.tap(find.text('Valider'));
      //await tester.pumpAndSettle();

      //debugDumpApp();

      await tester.tap(find.text('Créer mon compte'));
      await tester.pumpAndSettle();

      //debugDumpApp();


      //expect(find.byType(ProjectsUserPage), findsOneWidget);
      expect(find.text('Projets'), findsOneWidget);
    });
  });
}