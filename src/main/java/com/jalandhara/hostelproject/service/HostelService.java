package com.jalandhara.hostelproject.service;


import com.jalandhara.hostelproject.entity.*;
import com.jalandhara.hostelproject.exception.ResourceNotFoundException;
import com.jalandhara.hostelproject.mapper.*;
import com.jalandhara.hostelproject.repository.*;
import com.jalandhara.hostelproject.requestBean.*;
import com.jalandhara.hostelproject.responseBean.HostelResponseBean;
import com.jalandhara.hostelproject.responseBean.PersonResponseBean;
import com.jalandhara.hostelproject.responseBean.RoomResponseBean;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HostelService {

    @Autowired private PersonRepo personRepo;
    @Autowired private WifiRepo wifiRepo;
    @Autowired private JobRepo jobRepo;
    @Autowired private RoomRepo roomRepo;
    @Autowired private TvRepo tvRepo;

    @Autowired private PersonMapper personMapper;
    @Autowired private JobMapper jobMapper;
    @Autowired private RoomMapper roomMapper;
    @Autowired private TvMapper tvMapper;
    @Autowired private WifiMapper wifiMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public String savePerson(PersonRequestBean dto) {
        Room room = null;
        if (dto.getRoomId() != null) {
            room = roomRepo.findById(dto.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room not found with id " + dto.getRoomId()));
        }
        Wifi wifi = null;
        if (dto.getWifiId() != null) {
            wifi = wifiRepo.findById(dto.getWifiId()).orElseThrow(() -> new ResourceNotFoundException("Wifi not found with id " + dto.getWifiId()));
        }
        Set<Job> jobs = Collections.emptySet();
        if (dto.getJobIds() != null && !dto.getJobIds().isEmpty()) {
            jobs = new HashSet<>(jobRepo.findAllById(dto.getJobIds()));
            if (jobs.size() != dto.getJobIds().size()) {
                throw new ResourceNotFoundException("One or more jobs not found for given ids");
            }
        }
        Person person = personMapper.toEntity(dto,wifi,room,jobs);
        person.setAge(dto.getAge());
        personRepo.save(person);
        return "Person saved with id " + person.getId();
    }

    public String saveRoom(RoomRequestBean dto) {
        Tv tv = null;
        if (dto.getTv() != null) {
            tv = tvMapper.toEntity(dto.getTv());
        } else if (dto.getTvId() != null) {
            tv = tvRepo.findById(dto.getTvId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tv not found with id " + dto.getTvId()));
        }
        Room room = roomMapper.toEntity(dto, tv);
        roomRepo.save(room);
        return "Room with Tv saved with id " + room.getId();
    }

    public String saveJob(JobRequestBean dto) {
        Job job = jobMapper.toEntity(dto);
        jobRepo.save(job);
        return "Job saved with id " + job.getId();
    }

    public String saveWifi(WifiRequestBean dto) {
        Wifi wifi = wifiMapper.toEntity(dto);
        wifiRepo.save(wifi);
        return "Wifi saved with id " + wifi.getId();
    }

    public HostelResponseBean saveHostelWithAllMappings(HostelRequestBean hostelDto) {
        Room savedRoom = null;
        Wifi savedWifi = null;
        List<Job> savedJobs = new ArrayList<>();
        List<Person> savedPersons = new ArrayList<>();
        //1.ROOM 1-1
        if (hostelDto.getRoom() != null) {
            RoomRequestBean rdto = hostelDto.getRoom();
            Tv tv = null;
            if (rdto.getTv() != null) {
                TvRequestBean tvDto = rdto.getTv();
                if(tvDto.getId() != null) {
                    tv = tvRepo.findById(tvDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Tv not found"));
                    tv.setTvBrand(tvDto.getTvBrand());
                    tv.setTvSize(tvDto.getTvSize());
                    tv = tvRepo.save(tv);
                }else {
                    tv = tvRepo.save(tvMapper.toEntity(tvDto));
                }
            }
            if (rdto.getId() != null) {  // Reference
                savedRoom = roomRepo.findById(rdto.getId()).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
                // Update fields on savedRoom from DTO
                savedRoom.setRoomNo(rdto.getRoomNo());
                savedRoom.setFloorNo(rdto.getFloorNo());
                if (tv != null) {
                    savedRoom.setTv(tv);  // tv already handled above
                }
                savedRoom = roomRepo.save(savedRoom);
            } else { // New
                Room room = roomMapper.toEntity(rdto, tv);
                savedRoom = roomRepo.save(room);
            }
        }
        //2.WIFI 1-MANY
        if (hostelDto.getWifi() != null) {
            WifiRequestBean wdto = hostelDto.getWifi();
            if (wdto.getId() != null) {
                savedWifi = wifiRepo.findById(wdto.getId()).orElseThrow(() -> new ResourceNotFoundException("Wifi not found"));
                savedWifi.setWifiName(wdto.getWifiName());
                savedWifi.setWifiPassword(wdto.getWifiPassword());
                savedWifi = wifiRepo.save(savedWifi);
            } else {
                Wifi wifi = wifiMapper.toEntity(wdto);
                savedWifi = wifiRepo.save(wifi);
            }
        }
        //3.JOBS MANY-MANY
        if (hostelDto.getJobs() != null && !hostelDto.getJobs().isEmpty()) {
            for (JobRequestBean jobDto : hostelDto.getJobs()) {
                if (jobDto.getId() != null) {
                    Job j = jobRepo.findById(jobDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Job not found"));
                    // Update fields from DTO
                    j.setJobName(jobDto.getJobName());
                    j.setJobCompany(jobDto.getJobCompany());
                    // Save updated job
                    Job savedJob = jobRepo.save(j);
                    savedJobs.add(savedJob);
                } else {
                    Job newJob = jobRepo.save(jobMapper.toEntity(jobDto));
                    savedJobs.add(newJob);
                }
            }
        }
        //4.PERSONS MANY-1, 1-MANY, MANY-MANY
        if (hostelDto.getPersons() != null && !hostelDto.getPersons().isEmpty()) {
            for (PersonRequestBean pdto : hostelDto.getPersons()) {
                // For each person, resolve room/wifi/jobs as follows:
                Room personRoom = null;
                Wifi personWifi = null;
                Set<Job> personJobs = new HashSet<>();
                // Assign Room
                if (pdto.getRoomId() != null) {
                    personRoom = roomRepo.findById(pdto.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
                } else if (savedRoom != null) {
                    personRoom = savedRoom;
                }
                // Assign Wifi
                if (pdto.getWifiId() != null) {
                    personWifi = wifiRepo.findById(pdto.getWifiId()).orElseThrow(() -> new ResourceNotFoundException("Wifi not found"));
                } else if (savedWifi != null) {
                    personWifi = savedWifi;
                }
                // Assign Jobs (many-many)
                if (pdto.getJobIds() != null && !pdto.getJobIds().isEmpty()) {
                    personJobs.addAll(jobRepo.findAllById(pdto.getJobIds()));
                } else if (!savedJobs.isEmpty()) {
                    personJobs.addAll(savedJobs);
                }
                Person person = personMapper.toEntity(pdto, personWifi, personRoom, personJobs);
                // Don't forget to set age if you want …
                person.setAge(pdto.getAge());
                person = personRepo.save(person);
                savedPersons.add(person);
            }
        }
        // Prepare DTO response using mappers
        return HostelResponseBean.builder()
                .room(savedRoom != null ? roomMapper.toResponse(savedRoom) : null)
                .wifi(savedWifi != null ? wifiMapper.toResponse(savedWifi) : null)
                .jobs(savedJobs.stream().map(jobMapper::toResponse).toList())
                .persons(savedPersons.stream().map(personMapper::toResponse).toList())
                .build();
    }

    public Page<?> getAll(String type, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return switch (type.toLowerCase()){
            case "person" -> personRepo.findAll(pageable).map(personMapper::toResponse);
            case "room" -> roomRepo.findAll(pageable).map(roomMapper::toResponse);
            case "wifi" -> wifiRepo.findAll(pageable).map(wifiMapper::toResponse);
            case "job" -> jobRepo.findAll(pageable).map(jobMapper::toResponse);
            case "tv" -> tvRepo.findAll(pageable).map(tvMapper::toResponse);
            case "all" -> {
                Page<Person> personPage = personRepo.findAll(pageable);
                Page<HostelResponseBean> allData = personPage.map(
                        person -> HostelResponseBean.builder()
                                .person(personMapper.toResponse(person))
                                .room(person.getRoom() != null ? roomMapper.toResponse(person.getRoom()) : null)
                                .wifi(person.getWifi() != null ? wifiMapper.toResponse(person.getWifi()) : null)
                                .jobs(person.getJobs() != null ? person.getJobs().stream().map(jobMapper::toResponse).toList() : List.of())
                                .build());
                yield allData;
            }
            default -> throw new IllegalArgumentException("Invalid type" +  type);
        };
    }

    public Object getById(UUID id, String type){
        return switch (type.toLowerCase()){
            case "person" -> {
                Person person = personRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Not found"));
                yield  personMapper.toResponse(person);
            }
            case "room" -> {
                Room room = roomRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Not found"));
                yield  roomMapper.toResponse(room);
            }
            case "wifi" -> {
                Wifi wifi = wifiRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Not found"));
                yield  wifiMapper.toResponse(wifi);
            }
            case "tv" -> {
                Tv tv = tvRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Not found"));
                yield  tvMapper.toResponse(tv);
            }
            case "job" -> {
                Job job = jobRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Not found"));
                yield  jobMapper.toResponse(job);
            }
            default -> throw new IllegalArgumentException("Invalid type" +  type);
        };
    }

    public String deleteById(UUID id, String type){
        switch (type.toLowerCase()){
            case "person" -> personRepo.deleteById(id);
            case "room" -> {
                List<Person> personWithRoom = personRepo.findAllByRoomId(id);
                if(!personWithRoom.isEmpty()){
                    for(Person p : personWithRoom){
                        p.setRoom(null);
                    }
                    personRepo.saveAll(personWithRoom);
                }
                roomRepo.deleteById(id);
            }
            case "wifi" -> wifiRepo.deleteById(id);
            case "tv" -> {
                List<Room> roomsWithTv =roomRepo.findAll().stream()
                        .filter(room -> room.getTv() != null && room.getTv().getId().equals(id))
                        .toList();
                if(!roomsWithTv.isEmpty()){
                    roomsWithTv.forEach(r -> r.setTv(null));
                    roomRepo.saveAll(roomsWithTv);
                }
                tvRepo.deleteById(id);
            }
            case "job" -> {
                personRepo.deleteAllJobLinksById(id);
                jobRepo.deleteById(id);
            }
            default -> throw new IllegalArgumentException("Invalid type" +  type);
        }
        return type + " with id " + id + " deleted.";
    }

    @Transactional
    public String deleteAllData(){
        personRepo.deleteAllJobLinks();
        personRepo.deleteAll();
        roomRepo.deleteAll();
        tvRepo.deleteAll();
        wifiRepo.deleteAll();
        jobRepo.deleteAll();
        return "All data deleted successfully.";
    }

    @Transactional
    public HostelResponseBean updateHostel(UUID personId, HostelRequestBean dto) {
        // Fetch existing person (error if not found)
        Person existingPerson = personRepo.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
        PersonRequestBean personDto = dto.getPerson();
        Room updatedRoom = null;
        Tv updatedTv = null;
        Wifi updatedWifi = null;
        Set<Job> updatedJobs = new HashSet<>();

        // ===== Room & TV =====
        if (dto.getRoom() != null) {
            RoomRequestBean roomDto = dto.getRoom();
            if (roomDto.getId() != null) {
                updatedRoom = roomRepo.findById(roomDto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomDto.getId()));
            } else {
                // No ID -> create new Room
                updatedRoom = new Room();
            }
            // Update only non-null fields from DTO
            if (roomDto.getRoomNo() != null) updatedRoom.setRoomNo(roomDto.getRoomNo());
            if (roomDto.getFloorNo() != null) updatedRoom.setFloorNo(roomDto.getFloorNo());
            // TV in Room
            if (roomDto.getTv() != null) {
                TvRequestBean tvDto = roomDto.getTv();
                if (tvDto.getId() != null) {
                    updatedTv = tvRepo.findById(tvDto.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("TV not found with id: " + tvDto.getId()));
                } else {
                    updatedTv = new Tv();
                }
                if (tvDto.getTvBrand() != null) updatedTv.setTvBrand(tvDto.getTvBrand());
                if (tvDto.getTvSize() != null) updatedTv.setTvSize(tvDto.getTvSize());
                updatedTv = tvRepo.save(updatedTv);
                updatedRoom.setTv(updatedTv);
            }
            updatedRoom = roomRepo.save(updatedRoom); // Save new or updated room
        }
        // Direct TV update outside Room
        if (dto.getTv() != null) {
            TvRequestBean tvDto = dto.getTv();
            if (tvDto.getId() != null) {
                updatedTv = tvRepo.findById(tvDto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("TV not found with id: " + tvDto.getId()));
            } else {
                updatedTv = new Tv();
            }
            if (tvDto.getTvBrand() != null) updatedTv.setTvBrand(tvDto.getTvBrand());
            if (tvDto.getTvSize() != null) updatedTv.setTvSize(tvDto.getTvSize());
            updatedTv = tvRepo.save(updatedTv);

            // if updatedRoom exists and doesn't have this TV, set it
            if (updatedRoom != null) {
                updatedRoom.setTv(updatedTv);
                updatedRoom = roomRepo.save(updatedRoom);
            }
        }
        // If Room not provided, but person had a room, keep the old one
        if (updatedRoom == null && existingPerson.getRoom() != null) {
            updatedRoom = existingPerson.getRoom();
        }

        // ===== WiFi =====
        if (dto.getWifi() != null) {
            WifiRequestBean wifiDto = dto.getWifi();
            if (wifiDto.getId() != null) {
                updatedWifi = wifiRepo.findById(wifiDto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Wifi not found with id: " + wifiDto.getId()));
            } else {
                updatedWifi = new Wifi();
            }
            if (wifiDto.getWifiName() != null) updatedWifi.setWifiName(wifiDto.getWifiName());
            if (wifiDto.getWifiPassword() != null) updatedWifi.setWifiPassword(wifiDto.getWifiPassword());
            updatedWifi = wifiRepo.save(updatedWifi);
        }
        // WiFi from Person DTO
        if (personDto != null && personDto.getWifiId() != null) {
            updatedWifi = wifiRepo.findById(personDto.getWifiId())
                    .orElseThrow(() -> new ResourceNotFoundException("Wifi not found with id: " + personDto.getWifiId()));
        }
        // If WiFi not provided, but person had a WiFi, keep the old one
        if (updatedWifi == null && existingPerson.getWifi() != null) {
            updatedWifi = existingPerson.getWifi();
        }

        // ===== Jobs =====
        if (dto.getJobs() != null) {
            for (JobRequestBean jobDto : dto.getJobs()) {
                Job job;
                if (jobDto.getId() != null) {
                    job = jobRepo.findById(jobDto.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobDto.getId()));
                    // Update non-null fields
                    if (jobDto.getJobName() != null) job.setJobName(jobDto.getJobName());
                    if (jobDto.getJobCompany() != null) job.setJobCompany(jobDto.getJobCompany());
                } else {
                    job = jobMapper.toEntity(jobDto);
                }
                job = jobRepo.save(job);
                updatedJobs.add(job);
            }
        } else {
            // If no jobs provided, keep the existing jobs
            updatedJobs = existingPerson.getJobs() != null ? existingPerson.getJobs() : new HashSet<>();
        }

        // ===== Update Person =====
        Person updatedPerson = existingPerson;
        if (personDto != null) {
            if (personDto.getName() != null) updatedPerson.setName(personDto.getName());
            if (personDto.getAge() != null) updatedPerson.setAge(personDto.getAge());
            // You can handle additional fields here
        }
        updatedPerson.setRoom(updatedRoom);  // can be null (if all null, detach room)
        updatedPerson.setWifi(updatedWifi);  // can be null
        updatedPerson.setJobs(updatedJobs);
        updatedPerson = personRepo.save(updatedPerson);

        // ===== Prepare and return DTO (final output) =====
        return HostelResponseBean.builder()
                .person(personMapper.toResponse(updatedPerson))
                .room(updatedRoom != null ? roomMapper.toResponse(updatedRoom) : null)
                .tv(updatedTv != null ? tvMapper.toResponse(updatedTv) : null)
                .wifi(updatedWifi != null ? wifiMapper.toResponse(updatedWifi) : null)
                .jobs(updatedJobs.stream().map(jobMapper::toResponse).collect(Collectors.toList()))
                .build();
    }

    public Page<HostelResponseBean> getPaginatedHostel(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Person> personPage = personRepo.findAll(pageable);
        return personPage.map(person -> HostelResponseBean.builder()
                .person(personMapper.toResponse(person))
                .room(person.getRoom() != null ? roomMapper.toResponse(person.getRoom()) : null)
                .wifi(person.getWifi() != null ? wifiMapper.toResponse(person.getWifi()) : null)
                .jobs(person.getJobs() != null ? person.getJobs().stream().map(jobMapper::toResponse).toList() : List.of())
                .build()
        );
    }

    public List<PersonResponseBean> getPersonsByAgeAndName(
            Integer minAge, List<String> startLetters
    ) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Person> query = cb.createQuery(Person.class);
        Root<Person> root = query.from(Person.class);
        Predicate agePredicate = cb.greaterThanOrEqualTo(root.get("age"), minAge);
//        Predicate agePredicate = cb.greaterThanOrEqualTo(root.get("age"), 22);
        List<Predicate> namePredicates = new ArrayList<>();
//        namePredicates.add(cb.like(root.get("name"), "B%"));
//        namePredicates.add(cb.like(root.get("name"), "S%"));
//        namePredicates.add(cb.like(root.get("name"), "K%"));
//        namePredicates.add(cb.like(root.get("name"), "J%"));
        for (String letter : startLetters) {
            namePredicates.add(cb.like(root.get("name"),letter + "%"));
        }
        Predicate nameCondition = cb.or(namePredicates.toArray(new Predicate[0]));
        query.where(cb.and(agePredicate, nameCondition));
        List<Person> persons = entityManager.createQuery(query).getResultList();
        return persons.stream()
                .map(personMapper::toResponse) // Person → PersonResponseBean
                .toList();

    }

    public List<RoomResponseBean> getFilteredRooms(Integer minAge, Integer floorNo) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Room> query = cb.createQuery(Room.class);
        Root<Room> root = query.from(Room.class);
        Join<Room, Person> join = root.join("persons");
        Predicate agePredicate = cb.greaterThanOrEqualTo(join.get("age"), minAge);
        Predicate floorPredicate = cb.equal(root.get("floorNo"), floorNo);
        query.select(root).distinct(true).where(cb.and(agePredicate, floorPredicate));
        List<Room> rooms = entityManager.createQuery(query).getResultList();
        rooms.forEach(room -> {
            List<Person> filteredPersons = room.getPersons()
                    .stream()
                    .filter(p -> p.getAge() >= minAge)
                    .collect(Collectors.toList());
            room.setPersons(filteredPersons);
        });
        return rooms.stream()
                .map(roomMapper::toResponse) // Room → RoomResponseBean
                .toList();
    }

}
