package com.redmancometh.redcore.util;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.common.base.Function;
import com.redmancometh.redcore.RedCore;

public class SpecialFuture<T> {
	private static ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

	private Plugin plugin = RedCore.getPlugin(RedCore.class);
	private BukkitScheduler sync = Bukkit.getScheduler();
	private Lock waiting = new ReentrantLock();
	private AtomicReference<T> cache = new AtomicReference<>();
	private AtomicReference<Exception> exception = new AtomicReference<Exception>();
	private List<Consumer<T>> tasks = new CopyOnWriteArrayList<>();
	private List<Consumer<Exception>> exHandlers = new CopyOnWriteArrayList<>();

	public SpecialFuture(Supplier<T> s) {
		waiting.lock();
		Future<?> handler = pool.submit(() -> {
			try {
				T t = s.get();
				cache.set(t);
				for (Consumer<T> task : tasks) {
					sync.scheduleSyncDelayedTask(plugin, () -> {
						try {
							task.accept(t);
						} catch (Exception e) {
							Bukkit.getLogger().severe("SpecialFuture encountered an error while executing a task!");
							e.printStackTrace();
						}
					});
				}
				waiting.unlock();
			} catch (Exception e) {
				exception.set(e);
				waiting.unlock();
				sync.scheduleSyncDelayedTask(plugin, () -> {
					if (exHandlers.size() == 0) {
						e.printStackTrace();
						return;
					}
					for (Consumer<Exception> ce : exHandlers) {
						ce.accept(e);
					}
				});
			}
		});
		pool.schedule(new Runnable() {
			public void run() {
				if (!handler.isDone()) {
					handler.cancel(true);
					RuntimeException ex = new RuntimeException("Your task is taking way too fucking long. Fix it.");
					exception.set(ex);
					if (exHandlers.size() == 0) {
						ex.printStackTrace();
						return;
					}
					for (Consumer<Exception> ce : exHandlers) {
						ce.accept(ex);
					}
				}
			}
		}, 10000, TimeUnit.MILLISECONDS);
	}

	public static <T> SpecialFuture<T> supplyAsync(Supplier<T> s) {
		return new SpecialFuture<>(s);
	}

	public SpecialFuture<T> thenAccept(Consumer<T> c) {
		if (cache.get() == null) {
			tasks.add(c);
			return this;
		}
		c.accept(cache.get());
		return this;
	}

	public <U> SpecialFuture<U> thenApply(Function<T, U> func) {
		return SpecialFuture.supplyAsync(() -> {
			waiting.lock();
			waiting.unlock();
			T t = cache.get();
			if (t == null) {
				RuntimeException ex = new RuntimeException("Lock released without value, cann't apply! Look for other errors above!");
				sync.scheduleSyncDelayedTask(plugin, () -> {
					ex.printStackTrace();
				});
				throw ex;
			}
			BlockingQueue<U> queue = new ArrayBlockingQueue<>(1);
			sync.scheduleSyncDelayedTask(plugin, () -> {
				queue.add(func.apply(t));
			});
			U result;
			try {
				result = queue.poll(10, TimeUnit.SECONDS);
				if (result == null) {
					RuntimeException ex = new RuntimeException("Your function isn't done after 10 seconds? Wtf are you doing in there...?");
					sync.scheduleSyncDelayedTask(plugin, () -> {
						ex.printStackTrace();
					});
					throw ex;
				}
				return result;
			} catch (InterruptedException e) {
				throw new RuntimeException(e); // This can't happen I hope
			}
		});
	}

	public SpecialFuture<T> handleException(Consumer<Exception> handler) {
		if (cache.get() != null) {
			return this;
		}
		Exception ex = exception.get();
		if (ex != null) {
			handler.accept(ex);
			return this;
		}
		if (ex == null) {
			exHandlers.add(handler);
		}
		return this;
	}
}
