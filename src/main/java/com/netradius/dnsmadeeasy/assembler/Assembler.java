package com.netradius.dnsmadeeasy.assembler;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contact for all assemblers.
 *
 * @author Abhijeet C Kale
 */
public abstract class Assembler<F, T> {

  public abstract Class<T> getType();

  /**
   * Assembles an object from another.
   *
   * @param from the source object, or null
   * @return the created object or null if the source was null
   */
  @NonNull
  public T assemble(@NonNull F from) {
    try {
      T to = getType().newInstance();
      merge(from, to);
      return to;
    } catch (IllegalAccessException | InstantiationException x) {
      throw new IllegalStateException("Unable to instantiate class of type ["
          + getType().getCanonicalName() + "]");
    }
  }

  /**
   * Assembles a list of objects from the provided object sources.
   *
   * @param from the object sources
   * @return the assembled objects
   */
  @NonNull
  public List<T> assemble(@NonNull List<F> from) {
    return assemble(from, new ArrayList<>(from.size()));
  }

  /**
   * Assembles a set of objects from the provided object sources.
   *
   * @param from the object sources
   * @return the assembled objects
   */
  @NonNull
  public Set<T> assemble(@NonNull Set<F> from) {
    return assemble(from, new HashSet<>(from.size()));
  }

  /**
   * Assembles a collection of objects from the provided source collection
   * and adds them to the provided destination collection. The populated
   * destination collection is returned for convenience.
   *
   * @param from the source collection
   * @param to   the destination collection
   * @param <C>  the collection type
   * @return the destination collection
   */
  @NonNull
  public <C extends Collection<T>> C assemble(Collection<F> from, C to) {
    for (F fobj : from) {
      to.add(assemble(fobj));
    }
    return to;
  }

  @NonNull
  public List<T> assemble(Iterable<F> from) {
    ArrayList<T> to = new ArrayList<>();
    for (F f : from) {
      to.add(assemble(f));
    }
    return to;
  }

  /**
   * Merged a source object into another.
   *
   * @param from the source
   * @param to   the destination
   */
  public abstract void merge(@NonNull F from, @NonNull T to);

}
