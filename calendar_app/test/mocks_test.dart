import 'package:http/http.dart' as http;
import 'package:mockito/annotations.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'dart:convert';
import 'package:firebase_auth/firebase_auth.dart';

import 'mocks_test.mocks.dart';

@GenerateMocks([http.Client, FirebaseAuth, User, UserCredential])
void main() {}

Future<void> setupCommonMocks(
  MockClient client,
  MockFirebaseAuth mockAuth,
  MockUserCredential mockUserCredential,
) async {
  await dotenv.load(fileName: "lib/.env");

  when(client.get(Uri.parse('${dotenv.env['API_BASE_URL']}/professions')))
      .thenAnswer((_) async => http.Response.bytes(
            utf8.encode(jsonEncode([
              {"profession": "Danseur"},
              {"profession": "Comédien"},
              {"profession": "Musicien"},
            ])),
            200,
            headers: {'content-type': 'application/json; charset=utf-8'},
          ));

  when(client.delete(
    Uri.parse('${dotenv.env['API_BASE_URL']}/users/test1@mail.com'),
  )).thenAnswer((_) async => http.Response('', 204));

  when(client.post(
    Uri.parse('${dotenv.env['API_BASE_URL']}/users'),
    headers: anyNamed('headers'),
    body: anyNamed('body'),
  )).thenAnswer((invocation) async {
    final body = jsonDecode(invocation.namedArguments[#body] as String);
    if (body['email'] == 'test1@mail.com' &&
        body['firstName'] == 'Eve' &&
        body['lastName'] == 'Pley') {
      return http.Response(jsonEncode({'id': 1}), 201);
    } else {
      return http.Response('Invalid Data', 400);
    }
  });

  when(client.get(Uri.parse(
          '${dotenv.env['API_BASE_URL']}/users?email=test1@mail.com')))
      .thenAnswer((_) async => http.Response(
            jsonEncode({
              "firstName": "Eve",
              "lastName": "Pley",
              "email": "test1@mail.com",
              "id": 1
            }),
            200,
            headers: {'content-type': 'application/json; charset=utf-8'},
          ));

  when(client.get(
    Uri.parse('${dotenv.env['API_BASE_URL']}/users/test1@mail.com/professions'),
    headers: anyNamed('headers'),
  )).thenAnswer((_) async => http.Response.bytes(
        utf8.encode(jsonEncode(["Danseur", "Comédien", "Musicien"])),
        200,
        headers: {'content-type': 'application/json; charset=utf-8'},
      ));

  when(mockAuth.createUserWithEmailAndPassword(
    email: anyNamed('email'),
    password: anyNamed('password'),
  )).thenAnswer((_) async => mockUserCredential);

  final mockUser = MockUser();
  when(mockUser.email).thenReturn("test1@mail.com");
  when(mockAuth.currentUser).thenReturn(mockUser);
}
