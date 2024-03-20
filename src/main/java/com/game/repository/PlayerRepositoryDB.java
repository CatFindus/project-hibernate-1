package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory factory;
    public PlayerRepositoryDB() {
        factory = new Configuration().configure().buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try(Session session = factory.openSession()) {
            NativeQuery<Player> query = session.createNativeQuery("SELECT * FROM rpg.player LIMIT :limit OFFSET :offset", Player.class);
            query.setParameter("limit", pageSize);
            query.setParameter("offset", pageNumber*pageSize);
            return query.list();
        }
    }

    @Override
    public int getAllCount() {
        try(Session session = factory.openSession()) {
            Query<Long> query = session.createNamedQuery("player_getallcount", Long.class);
            long result = query.getSingleResult();
            return Math.toIntExact(result);
        }
    }

    @Override
    public Player save(Player player) {
        try(Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(player);
            transaction.commit();
        }
        return player;
    }

    @Override
    public Player update(Player player) {
        try(Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Player result = (Player) session.merge(player);
            transaction.commit();
            return result;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try(Session session = factory.openSession()) {
            Player player = session.get(Player.class, id);
            return Optional.ofNullable(player);
        }
    }

    @Override
    public void delete(Player player) {
        try(Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(player);
            transaction.commit();
        }
    }

    @PreDestroy
    public void beforeStop() {
        factory.close();
    }
}