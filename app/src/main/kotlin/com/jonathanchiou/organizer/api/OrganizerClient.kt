package com.jonathanchiou.organizer.api

import com.jonathanchiou.organizer.api.model.*
import com.jonathanchiou.organizer.main.ButtonModel
import com.jonathanchiou.organizer.main.MainFeedModel
import com.jonathanchiou.organizer.main.TitleModel
import com.jonathanchiou.organizer.scheduler.ClientEvent
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import retrofit2.Response
import java.util.concurrent.TimeUnit

class OrganizerClient(val organizerService: OrganizerService) {

    fun connect(googleIdToken: String): Observable<ApiUIModel<Token>> {
        return organizerService
            .connect(googleIdToken)
            .toUIModelStream()
    }

    fun getNotification(): Observable<ApiUIModel<Notification>> {
        return Observable.just(Response.success(Notification(title = "Respond to events",
                                                             text = "You've been invited to some " +
                                                                 "events. Please respond to them " +
                                                                 "as soon as possible!",
                                                             actionType = "Respond now",
                                                             actionCount = 3)))
            .delay(500, TimeUnit.MILLISECONDS)
            .toUIModelStream()
    }

    fun getEvents(): Observable<ApiUIModel<List<EventBlurb>>> {
        return Observable.just(Response.success(createEventBlurbs()))
            .delay(500, TimeUnit.MILLISECONDS)
            .toUIModelStream()
    }

    // TODO: Slightly better, but still kind of sucky.
    fun getMainFeed(): Observable<ApiUIModel<List<MainFeedModel>?>> {
        return Observable.zip(
            getNotification(),
            getEvents(),
            BiFunction<
                ApiUIModel<Notification>,
                ApiUIModel<List<EventBlurb>>,
                ApiUIModel<List<MainFeedModel>?>> { notificationUIModel, eventsUIModel ->
                var state = notificationUIModel.state
                if (eventsUIModel.state.ordinal < state.ordinal) {
                    state = eventsUIModel.state
                }

                val mainFeedModels = ArrayList<MainFeedModel>(3)
                notificationUIModel.model?.let {
                    mainFeedModels.add(it)
                }
                eventsUIModel.model?.let {
                    mainFeedModels.add(TitleModel("Upcoming"))
                    mainFeedModels.addAll(it)

                    if (it.size == EVENT_BLURB_MAIN_FEED_PAGE_SIZE) {
                        mainFeedModels.add(ButtonModel("Show more"))
                    }
                }

                return@BiFunction ApiUIModel(state, mainFeedModels)
            })
    }

    fun getPlaces(input: String, location: String?): Observable<ApiUIModel<List<Place>>> {
        return organizerService
            .getPlaces(if (!input.isEmpty()) input else null, location)
            .toUIModelStream()
    }

    fun searchAccounts(groupId: Int, query: String?): Observable<ApiUIModel<List<Account>>> {
        return Observable.just(Response.success(createAccounts()))
            .toUIModelStream()
    }

    fun createEvent(groupId: Int, clientEvent: ClientEvent): Observable<ApiUIModel<EventBlurb>> {
        return Observable.just(Response.success(EventBlurb(id = 42,
                                                           title = "",
                                                           date = 0,
                                                           creator = createAccount())))
            .toUIModelStream()
    }

    companion object {
        const val EVENT_BLURB_MAIN_FEED_PAGE_SIZE = 3
    }
}