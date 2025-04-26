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
  const String email = 'test1@mail.com';

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
    Uri.parse('${dotenv.env['API_BASE_URL']}/users/$email'),
  )).thenAnswer((_) async => http.Response('', 204));

  when(client.post(
    Uri.parse('${dotenv.env['API_BASE_URL']}/users'),
    headers: anyNamed('headers'),
    body: anyNamed('body'),
  )).thenAnswer((invocation) async {
    final body = jsonDecode(invocation.namedArguments[#body] as String);
    if (body['email'] == email &&
        body['firstName'] == 'Eve' &&
        body['lastName'] == 'Pley') {
      return http.Response(jsonEncode({'id': 1}), 201);
    } else {
      return http.Response('Invalid Data', 400);
    }
  });

  when(client
          .get(Uri.parse('${dotenv.env['API_BASE_URL']}/users?email=$email')))
      .thenAnswer((_) async => http.Response(
            jsonEncode({
              "firstName": "Eve",
              "lastName": "Pley",
              "email": email,
              "id": 1
            }),
            200,
            headers: {'content-type': 'application/json; charset=utf-8'},
          ));

  when(client.get(
    Uri.parse('${dotenv.env['API_BASE_URL']}/users/$email/professions'),
    headers: anyNamed('headers'),
  )).thenAnswer((_) async => http.Response.bytes(
        utf8.encode(jsonEncode(["Danseur", "Comédien"])),
        200,
        headers: {'content-type': 'application/json; charset=utf-8'},
      ));

  when(client.get(
    Uri.parse('${dotenv.env['API_BASE_URL']}/userProjects/organizer/$email'),
  )).thenAnswer((_) async => http.Response.bytes(
        utf8.encode(jsonEncode([
          {
            'id': 1,
            'name': 'Aladin',
            'description': 'Spectacle de danse et de chant',
            'beginningDate': '2024-04-01',
            'endingDate': '2024-04-10',
          },
          {
            'id': 2,
            'name': 'Un project de danse',
            'description': null,
            'beginningDate': null,
            'endingDate': null
          },
        ])),
        200,
        headers: {'content-type': 'application/json; charset=utf-8'},
      ));

  when(client.get(
    Uri.parse('${dotenv.env['API_BASE_URL']}/projects/1'),
  )).thenAnswer((_) async => http.Response.bytes(
        utf8.encode(jsonEncode({
          'id': 1,
          'name': 'Aladin',
          'description': 'Spectacle de danse et de chant',
          'beginningDate': '2024-04-01',
          'endingDate': '2024-04-10',
        })),
        200,
        headers: {'content-type': 'application/json; charset=utf-8'},
      ));

  when(client.get(
    Uri.parse('${dotenv.env['API_BASE_URL']}/projects/2'),
  )).thenAnswer((_) async => http.Response.bytes(
        utf8.encode(jsonEncode({
          'id': 2,
          'name': 'Un project de danse',
          'description': null,
          'beginningDate': null,
          'endingDate': null
        })),
        200,
        headers: {'content-type': 'application/json; charset=utf-8'},
      ));

  when(client.get(
    Uri.parse(
        '${dotenv.env['API_BASE_URL']}/projects/1/is-organizer?email=$email'),
  )).thenAnswer((_) async => http.Response.bytes(
        utf8.encode(jsonEncode({'isOrganizer': true})),
        200,
        headers: {'content-type': 'application/json; charset=utf-8'},
      ));

  when(client.get(
    Uri.parse(
        '${dotenv.env['API_BASE_URL']}/projects/2/is-organizer?email=$email'),
  )).thenAnswer((_) async => http.Response.bytes(
        utf8.encode(jsonEncode({'isOrganizer': true})),
        200,
        headers: {'content-type': 'application/json; charset=utf-8'},
      ));

  when(client.get(
    Uri.parse('${dotenv.env['API_BASE_URL']}/userProjects/1'),
  )).thenAnswer((_) async => http.Response.bytes(
        utf8.encode(jsonEncode([
          {
            'id': 1,
            'firstName': 'Eve',
            'lastName': 'Pley',
            'email': 'test1@mail.com',
          },
          {
            'id': 2,
            'firstName': 'Jean',
            'lastName': 'Bert',
            'email': 'test2@mail.com',
          },
        ])),
        200,
        headers: {'content-type': 'application/json; charset=utf-8'},
      ));

  when(client.get(
    Uri.parse('${dotenv.env['API_BASE_URL']}/roles'),
  )).thenAnswer((_) async => http.Response.bytes(
        utf8.encode(jsonEncode([
          {'role': 'Organisateur'},
          {'role': 'Danseur'},
          {'role': 'Non défini'},
          {"role": "Comédien"},
          {"role": "Musicien"},
        ])),
        200,
        headers: {'content-type': 'application/json; charset=utf-8'},
      ));

  when(client.get(
    Uri.parse('${dotenv.env['API_BASE_URL']}/projects/1/rehearsals'),
  )).thenAnswer((_) async => http.Response.bytes(
        utf8.encode(jsonEncode([
          {
            'id': 1,
            'name': 'Répétition générale',
            'description': 'Dernière répétition avec tous le monde',
            'date': null,
            'time': null,
            'duration': 'PT5H',
            'projectId': 1,
            'location': null,
            'participantsIds': []
          },
          {
            'id': 2,
            'name': 'Chorégraphie',
            'description': null,
            'date': null,
            'time': null,
            'duration': 'PT3H',
            'projectId': 1,
            'location': null,
            'participantsIds': []
          },
          {
            'id': 3,
            'name': 'Petite répétition',
            'description': null,
            'date': null,
            'time': '14:00:00',
            'duration': 'PT2H',
            'projectId': 1,
            'location': null,
            'participantsIds': []
          },
        ])),
        200,
        headers: {'content-type': 'application/json; charset=utf-8'},
      ));

  when(mockAuth.createUserWithEmailAndPassword(
    email: anyNamed('email'),
    password: anyNamed('password'),
  )).thenAnswer((_) async => mockUserCredential);

  final mockUser = MockUser();
  when(mockUser.email).thenReturn(email);
  when(mockAuth.currentUser).thenReturn(mockUser);
}
