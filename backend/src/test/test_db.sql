--
-- PostgreSQL database dump
--

-- Dumped from database version 17.0
-- Dumped by pg_dump version 17.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: cp_presence; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cp_presence (
    rehearsal_id bigint NOT NULL,
    user_id bigint NOT NULL,
    present boolean NOT NULL
);


ALTER TABLE public.cp_presence OWNER TO postgres;

--
-- Name: cp_presences; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cp_presences (
    rehearsal_id bigint NOT NULL,
    user_id bigint NOT NULL,
    present boolean NOT NULL
);


ALTER TABLE public.cp_presences OWNER TO postgres;

--
-- Name: cp_results; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cp_results (
    project_id bigint NOT NULL,
    rehearsal_id bigint NOT NULL,
    accepted boolean NOT NULL,
    beginning_date timestamp(6) without time zone NOT NULL
);


ALTER TABLE public.cp_results OWNER TO postgres;

--
-- Name: participations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.participations (
    rehearsal_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.participations OWNER TO postgres;

--
-- Name: professions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.professions (
    profession character varying(255) NOT NULL
);


ALTER TABLE public.professions OWNER TO postgres;

--
-- Name: projects; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.projects (
    id bigint NOT NULL,
    beginning_date date,
    description character varying(255),
    ending_date date,
    name character varying(255) NOT NULL
);


ALTER TABLE public.projects OWNER TO postgres;

--
-- Name: projects_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.projects_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.projects_id_seq OWNER TO postgres;

--
-- Name: projects_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.projects_id_seq OWNED BY public.projects.id;


--
-- Name: rehearsals; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rehearsals (
    id bigint NOT NULL,
    date date,
    description character varying(255),
    duration numeric(21,0) NOT NULL,
    location character varying(255),
    name character varying(255) NOT NULL,
    project_id bigint,
    "time" time without time zone
);


ALTER TABLE public.rehearsals OWNER TO postgres;

--
-- Name: rehearsals_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.rehearsals_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.rehearsals_id_seq OWNER TO postgres;

--
-- Name: rehearsals_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.rehearsals_id_seq OWNED BY public.rehearsals.id;


--
-- Name: rehearsals_participations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rehearsals_participations (
    rehearsal_id bigint NOT NULL,
    user_id bigint NOT NULL,
    present boolean NOT NULL
);


ALTER TABLE public.rehearsals_participations OWNER TO postgres;

--
-- Name: rehearsals_precedences; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rehearsals_precedences (
    current bigint NOT NULL,
    previous bigint NOT NULL
);


ALTER TABLE public.rehearsals_precedences OWNER TO postgres;

--
-- Name: rehearsals_presences; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rehearsals_presences (
    rehearsal_id bigint NOT NULL,
    user_id bigint NOT NULL,
    present boolean NOT NULL
);


ALTER TABLE public.rehearsals_presences OWNER TO postgres;

--
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.roles (
    role character varying(255) NOT NULL
);


ALTER TABLE public.roles OWNER TO postgres;

--
-- Name: user_projects; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_projects (
    project_id bigint NOT NULL,
    role character varying(255) NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.user_projects OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    first_name character varying(255) NOT NULL,
    last_name character varying(255) NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: users_professions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users_professions (
    profession character varying(255) NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.users_professions OWNER TO postgres;

--
-- Name: vacations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vacations (
    end_date date NOT NULL,
    start_date date NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.vacations OWNER TO postgres;

--
-- Name: weekly_availabilities; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.weekly_availabilities (
    end_time time without time zone NOT NULL,
    start_time time without time zone NOT NULL,
    user_id bigint NOT NULL,
    weekday integer NOT NULL
);


ALTER TABLE public.weekly_availabilities OWNER TO postgres;

--
-- Name: projects id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects ALTER COLUMN id SET DEFAULT nextval('public.projects_id_seq'::regclass);


--
-- Name: rehearsals id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rehearsals ALTER COLUMN id SET DEFAULT nextval('public.rehearsals_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: cp_presence; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.cp_presence (rehearsal_id, user_id, present) FROM stdin;
771	263	t
771	264	t
772	263	t
773	264	t
774	263	t
774	264	t
775	263	t
776	264	f
777	265	t
778	263	t
779	264	t
1	263	t
2	264	t
3	263	f
3	264	f
4	263	f
4	264	f
5	263	f
5	264	f
6	263	f
6	264	f
7	263	t
7	264	t
8	263	t
8	264	t
\.


--
-- Data for Name: cp_presences; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.cp_presences (rehearsal_id, user_id, present) FROM stdin;
777	265	t
778	263	t
779	264	t
1	263	t
2	264	t
5	263	f
5	264	f
6	263	f
6	264	f
7	263	t
7	264	t
8	263	t
8	264	t
772	263	t
3	263	f
3	264	f
4	263	f
4	264	f
776	264	f
771	264	t
773	264	t
774	263	t
774	264	t
775	263	t
771	263	f
780	266	t
780	267	t
781	266	t
782	266	t
783	267	t
781	267	t
788	266	t
788	267	t
789	266	t
789	267	t
790	266	t
791	267	t
784	266	t
784	267	t
785	266	t
785	267	t
786	266	t
787	267	t
\.


--
-- Data for Name: cp_results; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.cp_results (project_id, rehearsal_id, accepted, beginning_date) FROM stdin;
23	771	t	2025-04-01 10:00:00
23	773	t	2025-04-03 08:00:00
23	772	f	2025-04-01 10:00:00
28	3	t	2025-04-02 10:00:00
28	4	t	2025-04-03 22:30:00
32	784	f	2025-04-29 17:00:00
32	785	f	2025-04-29 14:00:00
29	5	f	2025-04-07 07:00:00
29	6	f	2025-04-07 07:00:00
32	786	f	2025-04-28 09:00:00
32	787	f	2025-05-01 10:00:00
25	777	f	2025-04-13 07:00:00
26	778	f	2025-04-08 10:00:00
26	779	f	2025-04-08 11:30:00
27	1	f	2025-04-08 11:30:00
27	2	f	2025-04-08 10:00:00
30	7	f	2025-04-08 10:00:00
30	8	f	2025-04-08 12:00:00
\.


--
-- Data for Name: participations; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.participations (rehearsal_id, user_id) FROM stdin;
771	263
771	264
772	263
773	264
774	263
774	264
775	263
776	264
777	265
778	263
779	264
1	263
2	264
3	263
3	264
4	263
4	264
5	263
5	264
6	263
6	264
7	263
7	264
8	263
8	264
780	266
780	267
781	266
781	267
782	266
783	267
784	266
784	267
785	266
785	267
786	266
787	267
788	266
788	267
789	266
789	267
790	266
791	267
\.


--
-- Data for Name: professions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.professions (profession) FROM stdin;
Agronomie et alimentation
Artisanat
Audiovisuel
Bâtiment
Culture
Commerce
Droit
Éducation
Finances et gestion
Politique
Scientifique
Santé
Sécurité
Technologies
Transports et logistique
Non défini
Artificier
Editeur musical
Maquilleur
Habilleur
Danseur
Comédien
Metteur en scène
Directeur artistique
Ingénieur du son
Costumier
Tour manager
Régisseur
Musicien
Eclairagiste
Chanteur
Artiste de cirque
Décorateur-scénographe
Technicien spectacle
Professeur de Danse
Formateur théâtre
Chef d'orchestre
Opérateur son
Figurant
Chorégraphe
Responsable logistique
Accessoiriste
Technicien des effets spéciaux
Responsable de l'accueil
Attaché de presse
Programmateur artistique
Directeur de production
\.


--
-- Data for Name: projects; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.projects (id, beginning_date, description, ending_date, name) FROM stdin;
23	2025-03-30	Winter show with santa...	2025-04-06	Christmas show
24	2025-03-30	Spring festival with live music	2025-04-02	Spring Festival
25	2025-03-30	Description of the new project...	2025-04-13	New Project
26	2025-04-06	A thrilling three-day circus performance featuring acrobats, fire breathers, and live music, perfect for families and entertainment lovers.	2025-04-08	Grand City Circus
27	2025-04-06	A captivating outdoor theater performance showcasing classic plays, perfect for drama enthusiasts.	2025-04-08	Outdoor Theater Festival
28	2025-04-01	A mesmerizing theatrical performance with stunning stage designs, captivating storytelling, and talented actors, perfect for theater enthusiasts.	2025-04-08	Theater Extravaganza
29	2025-04-06	A vibrant outdoor performance featuring classic plays.	2025-04-28	Outdoor Play
30	2025-04-06	A captivating dance performance with intricate choreography and beautiful costumes.	2025-04-13	Dance Showcase
31	2025-04-21	Contemporary dance showcase.	2025-04-27	Modern Moves
32	2025-04-21	Two-week theater performances.	2025-05-04	Stage Performances
33	2025-04-28	Week of ballet and drama.	2025-05-04	Still Dancing
\.


--
-- Data for Name: rehearsals; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rehearsals (id, date, description, duration, location, name, project_id, "time") FROM stdin;
772	\N	Rehearsal description for Rehearsal with only first participant	10800000000000	\N	Rehearsal with only first participant	23	\N
777	\N	Rehearsal for New Project	7200000000000	\N	Rehearsal for New Project	25	\N
778	\N	Rehearsal with only first participant for project 26	5400000000000	\N	Rehearsal with only first participant	26	\N
779	\N	Rehearsal with only second participant for project 26	5400000000000	\N	Rehearsal with only second participant	26	\N
1	\N	Rehearsal with only first participant for project 27	5400000000000	\N	Rehearsal with only first participant	27	\N
2	\N	Rehearsal with only second participant for project 27	5400000000000	\N	Rehearsal with only second participant	27	\N
3	2025-04-02	Rehearsal with both participants for project 27	7200000000000	\N	Rehearsal with both participants	28	10:00:00
4	2025-04-03	Rehearsal with both participants for project 27	10800000000000	\N	Rehearsal with both participants	28	22:30:00
5	2025-04-07	Rehearsal with both participants for project 29	7200000000000	\N	Rehearsal with both participants	29	\N
6	2025-04-07	Rehearsal with both participants for project 29	10800000000000	\N	Rehearsal with both participants	29	\N
7	\N	Rehearsal with both participants for project 30	7200000000000	\N	Rehearsal with both participants	30	10:00:00
8	\N	Rehearsal with both participants for project 30	3600000000000	\N	Rehearsal with both participants	30	12:00:00
784	\N	2h rehearsal with both participants	7200000000000	\N	Rehearsal with both participants	32	\N
785	\N	3h rehearsal with both participants	10800000000000	\N	Rehearsal with both participants	32	\N
786	\N	1h rehearsal with user 266 only	3600000000000	\N	Rehearsal with only first participant	32	\N
787	\N	2h30 rehearsal with user 267 only	9000000000000	\N	Rehearsal with only second participant	32	\N
788	\N	2h rehearsal with both participants	7200000000000	\N	Rehearsal with both participants	33	\N
789	\N	3h rehearsal with both participants	10800000000000	\N	Rehearsal with both participants	33	\N
790	\N	1h rehearsal with user 266 only	3600000000000	\N	Rehearsal with only first participant	33	\N
791	\N	2h30 rehearsal with user 267 only	9000000000000	\N	Rehearsal with only second participant	33	\N
771	\N	Rehearsal description for Rehearsal with both participants	10800000000000	\N	Rehearsal with both participants	23	\N
780	\N	2h rehearsal with both participants	7200000000000	\N	Rehearsal with both participants	31	\N
781	\N	3h rehearsal with both participants	10800000000000	\N	Rehearsal with both participants	31	\N
773	\N	Rehearsal description for Rehearsal with only second participant	10800000000000	\N	Rehearsal with only second participant	23	\N
782	\N	1h rehearsal with user 266 only	3600000000000	\N	Rehearsal with only first participant	31	\N
783	\N	2h30 rehearsal with user 267 only	9000000000000	\N	Rehearsal with only second participant	31	\N
774	\N	\N	10800000000000	\N	Rehearsal with both participants	24	\N
775	\N	Rehearsal with only first participant	14400000000000	\N	Rehearsal with only first participant	24	\N
776	\N	Rehearsal with only second participant	14400000000000	\N	Rehearsal with only second participant	24	\N
\.


--
-- Data for Name: rehearsals_participations; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rehearsals_participations (rehearsal_id, user_id, present) FROM stdin;
\.


--
-- Data for Name: rehearsals_precedences; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rehearsals_precedences (current, previous) FROM stdin;
779	778
1	2
\.


--
-- Data for Name: rehearsals_presences; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rehearsals_presences (rehearsal_id, user_id, present) FROM stdin;
776	264	f
780	266	f
780	267	f
781	266	f
781	267	f
782	266	f
783	267	f
774	263	t
774	264	t
775	263	t
788	266	f
788	267	f
789	266	f
789	267	f
790	266	f
791	267	f
\.


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.roles (role) FROM stdin;
Non défini
Artificier
Editeur musical
Maquilleur
Habilleur
Danseur
Comédien
Metteur en scène
Directeur artistique
Ingénieur du son
Costumier
Tour manager
Régisseur
Musicien
Eclairagiste
Chanteur
Artiste de cirque
Décorateur-scénographe
Technicien spectacle
Professeur de Danse
Formateur théâtre
Chef d'orchestre
Opérateur son
Figurant
Chorégraphe
Responsable logistique
Accessoiriste
Technicien des effets spéciaux
Responsable de l'accueil
Attaché de presse
Programmateur artistique
Directeur de production
Organizer
\.


--
-- Data for Name: user_projects; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_projects (project_id, role, user_id) FROM stdin;
23	Organizer	263
24	Organizer	263
23	Organizer	264
24	Organizer	264
25	Organizer	265
26	Organizer	263
26	Organizer	264
27	Organizer	263
27	Organizer	264
28	Organizer	263
28	Organizer	264
29	Organizer	263
29	Organizer	264
30	Organizer	263
30	Organizer	264
31	Organizer	266
32	Organizer	266
33	Organizer	266
31	Organizer	267
32	Organizer	267
33	Organizer	267
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, email, first_name, last_name) FROM stdin;
263	participant1@mail.com	participant1	lastNamep1
264	participant2@mail.com	participant2	lastNamep2
265	participant3@mail.com	participant3	lastNamep3
266	participant4@mail.com	participant4	lastNamep4
267	participant5@mail.com	participant5	lastNamep5
\.


--
-- Data for Name: users_professions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users_professions (profession, user_id) FROM stdin;
\.


--
-- Data for Name: vacations; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.vacations (end_date, start_date, user_id) FROM stdin;
2025-04-12	2025-03-30	265
\.


--
-- Data for Name: weekly_availabilities; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.weekly_availabilities (end_time, start_time, user_id, weekday) FROM stdin;
13:00:00	10:00:00	263	1
18:00:00	14:00:00	263	2
13:00:00	10:00:00	264	1
20:00:00	08:00:00	264	3
23:00:00	07:00:00	265	0
23:00:00	07:00:00	265	1
23:00:00	07:00:00	265	2
23:00:00	07:00:00	265	3
23:00:00	07:00:00	265	4
23:00:00	07:00:00	265	5
23:00:00	07:00:00	265	6
10:00:00	09:00:00	266	0
19:00:00	14:00:00	266	1
19:00:00	14:00:00	267	1
12:30:00	10:00:00	267	3
\.


--
-- Name: projects_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.projects_id_seq', 33, true);


--
-- Name: rehearsals_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rehearsals_id_seq', 791, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 267, true);


--
-- Name: cp_presence cp_presence_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cp_presence
    ADD CONSTRAINT cp_presence_pkey PRIMARY KEY (rehearsal_id, user_id);


--
-- Name: cp_presences cp_presences_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cp_presences
    ADD CONSTRAINT cp_presences_pkey PRIMARY KEY (rehearsal_id, user_id);


--
-- Name: cp_results cp_results_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cp_results
    ADD CONSTRAINT cp_results_pkey PRIMARY KEY (project_id, rehearsal_id);


--
-- Name: participations participations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participations
    ADD CONSTRAINT participations_pkey PRIMARY KEY (rehearsal_id, user_id);


--
-- Name: professions professions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.professions
    ADD CONSTRAINT professions_pkey PRIMARY KEY (profession);


--
-- Name: projects projects_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_pkey PRIMARY KEY (id);


--
-- Name: rehearsals_participations rehearsals_participations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rehearsals_participations
    ADD CONSTRAINT rehearsals_participations_pkey PRIMARY KEY (rehearsal_id, user_id);


--
-- Name: rehearsals rehearsals_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rehearsals
    ADD CONSTRAINT rehearsals_pkey PRIMARY KEY (id);


--
-- Name: rehearsals_precedences rehearsals_precedences_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rehearsals_precedences
    ADD CONSTRAINT rehearsals_precedences_pkey PRIMARY KEY (current, previous);


--
-- Name: rehearsals_presences rehearsals_presences_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rehearsals_presences
    ADD CONSTRAINT rehearsals_presences_pkey PRIMARY KEY (rehearsal_id, user_id);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (role);


--
-- Name: users uk_6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: user_projects user_projects_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_projects
    ADD CONSTRAINT user_projects_pkey PRIMARY KEY (project_id, role, user_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users_professions users_professions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users_professions
    ADD CONSTRAINT users_professions_pkey PRIMARY KEY (profession, user_id);


--
-- Name: vacations vacations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vacations
    ADD CONSTRAINT vacations_pkey PRIMARY KEY (end_date, start_date, user_id);


--
-- Name: weekly_availabilities weekly_availabilities_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.weekly_availabilities
    ADD CONSTRAINT weekly_availabilities_pkey PRIMARY KEY (end_time, start_time, user_id, weekday);


--
-- Name: participations fk166yf958qjqf8uoslyuk9e19p; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participations
    ADD CONSTRAINT fk166yf958qjqf8uoslyuk9e19p FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: rehearsals_presences fk2nuqo2m9647fe4idnrr09lhxc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rehearsals_presences
    ADD CONSTRAINT fk2nuqo2m9647fe4idnrr09lhxc FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: rehearsals_precedences fk32eisr1618hhrfxhaltghyufq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rehearsals_precedences
    ADD CONSTRAINT fk32eisr1618hhrfxhaltghyufq FOREIGN KEY (current) REFERENCES public.rehearsals(id) ON DELETE CASCADE;


--
-- Name: rehearsals_participations fk6h0in3w0icm0pseh5q58yovdm; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rehearsals_participations
    ADD CONSTRAINT fk6h0in3w0icm0pseh5q58yovdm FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: participations fk7bjg4bbd0w54jnh6f44qx19qr; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participations
    ADD CONSTRAINT fk7bjg4bbd0w54jnh6f44qx19qr FOREIGN KEY (rehearsal_id) REFERENCES public.rehearsals(id) ON DELETE CASCADE;


--
-- Name: cp_results fk8g1jm5brmaaid8kf8yvq2biaj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cp_results
    ADD CONSTRAINT fk8g1jm5brmaaid8kf8yvq2biaj FOREIGN KEY (rehearsal_id) REFERENCES public.rehearsals(id) ON DELETE CASCADE;


--
-- Name: weekly_availabilities fk8rkpns02v8vg3yon1ibpercoq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.weekly_availabilities
    ADD CONSTRAINT fk8rkpns02v8vg3yon1ibpercoq FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: cp_presence fkasgecg7rmx2tkn7ji25jf9aif; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cp_presence
    ADD CONSTRAINT fkasgecg7rmx2tkn7ji25jf9aif FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: rehearsals_presences fkaxoaso9voyu69gclrlbcv04li; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rehearsals_presences
    ADD CONSTRAINT fkaxoaso9voyu69gclrlbcv04li FOREIGN KEY (rehearsal_id) REFERENCES public.rehearsals(id) ON DELETE CASCADE;


--
-- Name: cp_presences fkb8j1td46pure34wox5b3nojhm; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cp_presences
    ADD CONSTRAINT fkb8j1td46pure34wox5b3nojhm FOREIGN KEY (rehearsal_id) REFERENCES public.rehearsals(id) ON DELETE CASCADE;


--
-- Name: cp_presences fkg02xhep4555fefd00rxypffy1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cp_presences
    ADD CONSTRAINT fkg02xhep4555fefd00rxypffy1 FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: cp_presence fkiwlaejjgyo5t17km62kmpx8xl; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cp_presence
    ADD CONSTRAINT fkiwlaejjgyo5t17km62kmpx8xl FOREIGN KEY (rehearsal_id) REFERENCES public.rehearsals(id) ON DELETE CASCADE;


--
-- Name: users_professions fkkqkpkusn3ib3px8ky09yx1wpd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users_professions
    ADD CONSTRAINT fkkqkpkusn3ib3px8ky09yx1wpd FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: rehearsals_precedences fkm4a6rdf5eq2x7qtdw2trt9i9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rehearsals_precedences
    ADD CONSTRAINT fkm4a6rdf5eq2x7qtdw2trt9i9 FOREIGN KEY (previous) REFERENCES public.rehearsals(id) ON DELETE CASCADE;


--
-- Name: user_projects fkmpj50a8pj7hp5kvs73mt8hh5h; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_projects
    ADD CONSTRAINT fkmpj50a8pj7hp5kvs73mt8hh5h FOREIGN KEY (role) REFERENCES public.roles(role) ON DELETE CASCADE;


--
-- Name: users_professions fkn0oli5a2rbajw6i39m9x9891g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users_professions
    ADD CONSTRAINT fkn0oli5a2rbajw6i39m9x9891g FOREIGN KEY (profession) REFERENCES public.professions(profession) ON DELETE CASCADE;


--
-- Name: rehearsals fkng10nb5av3wirkx4x9gb1vxjo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rehearsals
    ADD CONSTRAINT fkng10nb5av3wirkx4x9gb1vxjo FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


--
-- Name: user_projects fkof7c4wufgerxtl9moqol6c516; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_projects
    ADD CONSTRAINT fkof7c4wufgerxtl9moqol6c516 FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


--
-- Name: rehearsals_participations fkqpadkxf9pdk1ekc25yps7jn5f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rehearsals_participations
    ADD CONSTRAINT fkqpadkxf9pdk1ekc25yps7jn5f FOREIGN KEY (rehearsal_id) REFERENCES public.rehearsals(id) ON DELETE CASCADE;


--
-- Name: cp_results fkqypbvw26b7ohtbci9bcwvt2jp; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cp_results
    ADD CONSTRAINT fkqypbvw26b7ohtbci9bcwvt2jp FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE CASCADE;


--
-- Name: user_projects fkr25ilmlcm8ugp8i3rogl6jp0l; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_projects
    ADD CONSTRAINT fkr25ilmlcm8ugp8i3rogl6jp0l FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

