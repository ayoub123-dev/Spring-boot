# Atelier 5 : Spring MVC, API REST, Cool Request et SpringDoc

## A. Endpoints

| Contrôleur | Verbe et route | Service appelé | Succès | Erreurs |
| :--- | :--- | :--- | :--- | :--- |
| AgenceController | GET /api/agences | IAgenceService.findAll() | 200 | — |
| AgenceController | GET /api/agences/{id} | IAgenceService.findById(id) | 200 | 404 |
| AgenceController | POST /api/agences | IAgenceService.create(obj) | 201 | 400 |
| AgenceController | PUT /api/agences/{id} | IAgenceService.update(id, obj) | 200 | 404, 400 |
| AgenceController | DELETE /api/agences/{id} | IAgenceService.deleteById(id) | 204 | 404 |
| EmployeController | GET /api/employes | IEmployeService.findAll() | 200 | — |
| EmployeController | GET /api/employes/{id} | IEmployeService.findById(id) | 200 | 404 |
| EmployeController | POST /api/employes | IEmployeService.create(obj) | 201 | 400 |
| EmployeController | PUT /api/employes/{id} | IEmployeService.update(id, obj) | 200 | 404, 400 |
| EmployeController | DELETE /api/employes/{id} | IEmployeService.deleteById(id) | 204 | 404 |
| VehiculeController | GET /api/vehicules | IVehiculeService.findAll() | 200 | — |
| VehiculeController | GET /api/vehicules/{id} | IVehiculeService.findById(id) | 200 | 404 |
| VehiculeController | POST /api/vehicules | IVehiculeService.create(obj) | 201 | 400 |
| VehiculeController | PUT /api/vehicules/{id} | IVehiculeService.update(id, obj) | 200 | 404, 400 |
| VehiculeController | DELETE /api/vehicules/{id} | IVehiculeService.deleteById(id) | 204 | 404 |
| EquipementController | GET /api/equipements | IEquipementService.findAll() | 200 | — |
| EquipementController | GET /api/equipements/{id} | IEquipementService.findById(id) | 200 | 404 |
| EquipementController | POST /api/equipements | IEquipementService.create(obj) | 201 | 400 |
| EquipementController | PUT /api/equipements/{id} | IEquipementService.update(id, obj) | 200 | 404, 400 |
| EquipementController | DELETE /api/equipements/{id} | IEquipementService.deleteById(id) | 204 | 404 |
| ClientController | GET /api/clients | IClientService.findAll() | 200 | — |
| ClientController | GET /api/clients/{id} | IClientService.findById(id) | 200 | 404 |
| ClientController | POST /api/clients | IClientService.create(obj) | 201 | 400 |
| ClientController | PUT /api/clients/{id} | IClientService.update(id, obj) | 200 | 404, 400 |
| ClientController | DELETE /api/clients/{id} | IClientService.deleteById(id) | 204 | 404 |
| ReservationController | GET /api/reservations | IReservationService.findAll() | 200 | — |
| ReservationController | GET /api/reservations/{id} | IReservationService.findById(id) | 200 | 404 |
| ReservationController | POST /api/reservations | IReservationService.create(obj) | 201 | 400 |
| ReservationController | PUT /api/reservations/{id} | IReservationService.update(id, obj) | 200 | 404, 400 |
| ReservationController | DELETE /api/reservations/{id} | IReservationService.deleteById(id) | 204 | 404 |
| ContratController | GET /api/contrats | IContratService.findAll() | 200 | — |
| ContratController | GET /api/contrats/{id} | IContratService.findById(id) | 200 | 404 |
| ContratController | POST /api/contrats | IContratService.create(obj) | 201 | 400 |
| ContratController | PUT /api/contrats/{id} | IContratService.update(id, obj) | 200 | 404, 400 |
| ContratController | DELETE /api/contrats/{id} | IContratService.deleteById(id) | 204 | 404 |
| MaintenanceController | GET /api/maintenances | IMaintenanceService.findAll() | 200 | — |
| MaintenanceController | GET /api/maintenances/{id} | IMaintenanceService.findById(id) | 200 | 404 |
| MaintenanceController | POST /api/maintenances | IMaintenanceService.create(obj) | 201 | 400 |
| MaintenanceController | PUT /api/maintenances/{id} | IMaintenanceService.update(id, obj) | 200 | 404, 400 |
| MaintenanceController | DELETE /api/maintenances/{id} | IMaintenanceService.deleteById(id) | 204 | 404 |
| PaiementController | GET /api/paiements | IPaiementService.findAll() | 200 | — |
| PaiementController | GET /api/paiements/{id} | IPaiementService.findById(id) | 200 | 404 |
| PaiementController | POST/PUT/DELETE /api/paiements | (aucun) | — | 405 |

Le préfixe `/api` est défini une seule fois par `server.servlet.context-path=/api`. Les 404 viennent de `ResourceNotFoundException` et les 400 de `IllegalArgumentException`, toutes deux traduites par `GlobalExceptionHandler` ; les contrôleurs ne produisent aucun code d'erreur eux-mêmes.

---

## B. Annotations Jackson

| Association | Annotation retenue | Justification |
| :--- | :--- | :--- |
| `Contrat.paiements` ↔ `Paiement.contrat` | `@JsonManagedReference("contrat-paiements")` côté liste, `@JsonBackReference("contrat-paiements")` côté parent | Vraie composition (`cascade = ALL`) : le paiement se crée dans le JSON du contrat, et Jackson renseigne automatiquement `paiement.contrat` à la lecture. |
| `Agence.employes`, `Agence.vehicules` | `@JsonIgnore` | Côté inverse (`mappedBy`) : évite la boucle Agence → Employé/Véhicule → Agence. L'enfant désigne son agence par `{ "agence": { "idAgence": 1 } }`. |
| `Client.reservations` | `@JsonIgnore` | Côté inverse. `Reservation.client` n'a aucune annotation pour que le POST conserve le client. |
| `Vehicule.maintenances`, `Vehicule.reservations` | `@JsonIgnore` | Côté inverse des associations vers Maintenance et Reservation. |
| `Equipement.vehicules` | `@JsonIgnore` | Côté inverse du `@ManyToMany` ; `Vehicule.equipements` (propriétaire) reste sérialisé. |
| `Reservation.contrat` | `@JsonIgnore` | Côté inverse du `@OneToOne` ; `Contrat.reservation` (propriétaire) reste sérialisé. |

**Différence entre `@JsonBackReference` et `@JsonIgnore`** : `@JsonBackReference` ignore la propriété à la sérialisation mais la **reconstruit à la désérialisation** (le lien enfant → parent est rempli automatiquement). `@JsonIgnore` ignore la propriété dans les deux sens : elle n'est ni écrite ni lue. C'est pourquoi `@JsonBackReference` ne doit pas être posé sur `Reservation.client` : la valeur serait ignorée à la lecture et la réservation serait créée sans client.

---

## C. Cool Request et Swagger

| Outil | Ce qu'il apporte | Exemple rencontré dans l'atelier |
| :--- | :--- | :--- |
| **Cool Request** (plugin IntelliJ) | Détecte les contrôleurs, envoie des requêtes depuis l'IDE et affiche statut et corps de la réponse. Sert à **tester** et à rejouer les requêtes. | `GET /api/contrats/9999` : 404 avec corps JSON (statut, message, horodatage). |
| **Swagger UI** (SpringDoc) | Documentation générée depuis le code et synchronisée avec lui ; permet d'explorer et d'essayer les endpoints. Sert à **documenter**. | Groupes « Contrats » et « Paiements », avec réponses 200/201/204/400/404 décrites par `@Tag`, `@Operation` et `@ApiResponse`. |

Les deux sont complémentaires : l'un vérifie le comportement, l'autre le décrit pour les autres développeurs.

---

## D. Anomalies SonarQube

| Anomalie | Règle / explication | Correction |
| :--- | :--- | :--- |
| Entités JPA utilisées comme paramètres de `@RequestBody` dans les contrôleurs | `java:S4684` : une entité persistante exposée directement couple l'API à la base et permet de modifier des champs internes. | Provisoire dans cet atelier ; sera remplacée par des DTO au Chapitre 10. |
| *(à compléter avec le résultat de votre propre analyse)* | | |

Lancez « Analyze with SonarQube for IDE » sur `web.controller` et reportez ici au moins une autre anomalie réellement relevée, avec sa correction.

---

## E. Questions de compréhension

1. **Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode du contrôleur ?**
   Le `DispatcherServlet` est le point d'entrée unique. Il interroge le `HandlerMapping`, qui identifie le contrôleur et la méthode à partir du verbe HTTP et de l'URL (`@RequestMapping` de la classe + `@GetMapping`, `@PostMapping`... de la méthode).

2. **Pourquoi `ContratController` dépend-il de `IContratService` et non de `ContratRepository` ni de `ContratServiceImpl` ?**
   Le contrôleur doit rester mince et ne contenir aucune logique métier ni accès aux données : il délègue à la couche Service. En dépendant de l'interface, on respecte l'inversion des dépendances (couplage faible) : on peut changer l'implémentation ou la mocker sans toucher au contrôleur. Dépendre du repository court-circuiterait la logique métier du service.

3. **Pourquoi un POST renvoie-t-il 201 et un DELETE 204 ? Pourquoi une ressource absente donne-t-elle 404 et non 500 ?**
   201 signifie « ressource créée » et 204 « succès sans corps de réponse » (rien à retourner après une suppression). Une ressource absente est une erreur du client (mauvais identifiant), pas une panne du serveur : `ResourceNotFoundException` est traduite en 404 par le `GlobalExceptionHandler`, alors qu'une exception non gérée donnerait un 500.

4. **Pourquoi `PaiementController` n'expose-t-il que des GET ? Quelle réponse reçoit un POST `/api/paiements` ?**
   Un paiement fait partie du cycle de vie d'un contrat (composition, `cascade = ALL`) : il est créé, modifié et supprimé via le contrat, pour préserver l'intégrité. Un POST sur `/api/paiements` reçoit **405 Method Not Allowed**, car aucune méthode du contrôleur n'est associée à ce verbe.

5. **Quels risques présente l'exposition directe des entités JPA ? Pourquoi `Contrat.paiements` utilise-t-il `@JsonManagedReference` alors que `Agence.vehicules` utilise `@JsonIgnore` ?**
   Risques : couplage entre l'API et le schéma de base, champs internes visibles, boucles de sérialisation sur les associations bidirectionnelles, proxies LAZY non sérialisables, et un client qui peut renseigner des champs qu'il ne devrait pas modifier. `Contrat.paiements` est une composition : le paiement n'existe pas sans son contrat et se crée dans son JSON, d'où la paire managed/back qui reconstruit le lien. `Agence.vehicules` est le côté inverse d'une association où le véhicule est créé seul et désigne son agence par identifiant : la liste n'a pas besoin d'être sérialisée, donc `@JsonIgnore` suffit.
